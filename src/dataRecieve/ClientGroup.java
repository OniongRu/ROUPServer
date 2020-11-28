package dataRecieve;

import DBManager.DBManager;
import GUI.Controller;
import GUI.PrettyException;
import com.google.gson.JsonPrimitive;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.*;
import java.lang.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

//класс объекта, работающего в отдельном потоке, взаимодействующий с Сокетами,
//записанными в его селектор
public class ClientGroup extends Thread{
    private Selector SelectorS;
    int PORT;
    private int clientAm = 0;
    private Queue<DataPack> dataPackQueue;
    private boolean isCloseSent = false;

    private void incrementCnt(){
        clientAm++;
    }

    private void decrementCnt(){
        clientAm--;
    }

    private void resetCnt(){
        clientAm = 0;
    }

    public int getClientAm(){
        return clientAm;
    }

    //конструктор
    public ClientGroup(SocketChannel ServElSoc, int PORT, Queue<DataPack> dataPackQueue) throws PrettyException {
        try {
            SelectorS = Selector.open();
            ServElSoc.configureBlocking(false);
            ServElSoc.register(SelectorS, SelectionKey.OP_READ);
            System.out.println(ServElSoc.getRemoteAddress() + " ♫CONNECTED♫");
        }catch(IOException e){
            throw new PrettyException(e, "Error creating ClientGroup");
        }
        this.PORT = PORT;
        resetCnt();
        incrementCnt();
        this.dataPackQueue = dataPackQueue;
        start();
    }

    //добавление нового сокета в селектор
    public void AddSocket(SocketChannel ClientS) throws IOException {
        ClientS.configureBlocking(false);
        ClientS.register(SelectorS, SelectionKey.OP_READ);
        System.out.println(ClientS.getRemoteAddress()+" ♫CONNECTED♫");
        incrementCnt();
        SelectorS.wakeup();
    }

    public void run() {
        try {
            isCloseSent = false;
            while (true) {

                try {
                    SelectorS.select(); //ожидание действий от клиентов
                }catch(IOException e){
                    throw new PrettyException(e, "Error managing client group");
                }

                if (isCloseSent){
                    try{
                    closeClientGroup();
                    }catch(IOException e){
                        throw new PrettyException(e, "Error closing client group");
                    }
                    return;
                }

                Set<SelectionKey> selectedKeys = SelectorS.selectedKeys(); //создание ключей для соединений, от которых пришли запросы
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        takeGson(key);
                    }
                    iter.remove();
                }
           }
        }catch (PrettyException e) {
            throw new RuntimeException(e.getPrettyMessage());
        }
    }

    public void closeClientGroup() throws IOException {
        if (SelectorS != null) {
            SelectorS.close();
        }
        SelectorS = null;
        if (dataPackQueue != null)
            dataPackQueue.clear();
        dataPackQueue = null;
    }

    public void sendClose(){
        isCloseSent = true;
        SelectorS.wakeup();
    }

    //TODO - delete this when debug not needed
    public byte[] getPBKDF2SecurePassword(String userName, String password) {
        try {
            byte[] salt;
            salt = "defaultPassword".getBytes();
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Controller.getInstance().showErrorMessage("Can't encrypt password:\nAlgorithm or KeySpec exception");
            Controller.getInstance().onTurnedOff();
            return null;
        } catch (Exception e) {
            Controller.getInstance().showErrorMessage("Can't encrypt password\nUnknown reason");
            Controller.getInstance().onTurnedOff();
            return null;
        }
    }

    //приём json-а от клиента и преобразование в объекта dataReceive.DataPack
    //In this application there is an agreement "EndThisConnection" in a beginning means stop signal
    //Beginning "Client data\n" means that server is about to receive DataPack
    //Beginning "Request\n" means that server is about to receive a query for an administrator's client
    private void takeGson(SelectionKey key) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024*10);
            try {
                client.read(buffer);
            }
            catch (IOException e) {
                decrementCnt();
                try {
                    System.out.println(((SocketChannel)key.channel()).getRemoteAddress() + " #DISCONNECTED# from thread" + currentThread().getId() + " ");
                    key.channel().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            String clientData = new String(buffer.array()).trim();
            buffer.clear();

            /*//TODO - delete when debug finishes
            byte[] passwordBytes = getPBKDF2SecurePassword("", "");
            clientData = "Register client sender\nlogin\n";
            clientData += Base64.getEncoder().encodeToString(passwordBytes);*/


            if(clientData.equals("EndThisConnection")) {//TODO Add types of getting data
                decrementCnt();
                ParseJSON.EndConnection(key);
            }
            else if (clientData.startsWith("Client data\n")) {
                System.out.println(clientData);
                DataPack dataPackFromUser = ParseJSON.ClSenderData(clientData);
                DBManager manager = new DBManager();
                try {
                    if (manager.isUserValid(dataPackFromUser.getUserName(), dataPackFromUser.getPassword())) {
                        dataPackQueue.add(dataPackFromUser);
                        buffer.put("Data is being processed".getBytes());
                    } else {
                        buffer.put("Data is ignored".getBytes());
                    }
                } catch (SQLException e) {
                    buffer.clear();
                    buffer.put("Data is ignored".getBytes());
                    Controller.getInstance().showErrorMessage("Could not check if user exists in database");
                } finally {
                    buffer.flip();
                    try {
                        client.write(buffer);
                    } catch (IOException e) {
                        Controller.getInstance().showErrorMessage("Could not send respond client\ndata status");
                    }
                }
            }
            else if (clientData.startsWith("Request\n")) {
                //TODO handle requests
            }
            else if (clientData.startsWith("Register client sender\n")) {
                //Respond register status
                if (ParseJSON.RegisterClSender(clientData)) {
                    buffer.put("Register successful".getBytes());
                } else {
                    buffer.put("Register failed".getBytes());
                }
                buffer.flip();
                try {
                    client.write(buffer);
                } catch (IOException e) {
                    Controller.getInstance().showErrorMessage("Could not send respond client\nregister status");
                }
            }
    }
}
