package dataRecieve;

import DBManager.DBManager;
import GUI.Controller;
import GUI.PrettyException;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import dataSend.UserProgramNamesWrapper;
import databaseInteract.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

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

    /*//TODO - delete this when debug not needed
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
    }*/

    //приём json-а от клиента и преобразование в объекта dataReceive.DataPack
    //In this application there is an agreement "EndThisConnection" in a beginning means stop signal
    //Beginning "Client data\n" means that server is about to receive DataPack
    //Beginning "Request\n" means that server is about to receive a query for an administrator's client
    private void takeGson(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer requestBuffer = ByteBuffer.allocate(1024 * 10);
        requestBuffer.clear();
        try {
            client.read(requestBuffer);
        } catch (IOException e) {
            decrementCnt();
            try {
                System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED# from thread" + currentThread().getId() + " ");
                key.channel().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        String clientData = new String(requestBuffer.array()).trim();
        //clientData = clientData.substring(4);
        requestBuffer.clear();
        //clientData = "NeedJson\n {\"name\": \"\", \"password\": \"nYTQ4q/9v8UcKK64U2cz9g==\", \"users\": [\"Goose\"], \"programs\":[\"sihost.exe\", \"svchost.exe\", \"idea64.exe\"], \"from\": \"00:00:00, 01.01.2000\", \"to\": \"00:00:00, 01.01.2050\"}";

        ByteBuffer respondBuffer = ByteBuffer.allocate(1024 * 10);

        if (clientData.equals("EndThisConnection")) {//TODO Add types of getting data
            decrementCnt();
            ParseJSON.EndConnection(key);
        } else if (clientData.startsWith("Client data\n")) {
            System.out.println(clientData);
            DataPack dataPackFromUser = ParseJSON.ClSenderData(clientData);
            DBManager manager = new DBManager();
            try {
                if (manager.isUserValid(dataPackFromUser.getUserName(), dataPackFromUser.getPassword())) {
                    dataPackQueue.add(dataPackFromUser);
                    respondBuffer.put("Data is being processed".getBytes(StandardCharsets.UTF_8));
                } else {
                    respondBuffer.put("Data is ignored".getBytes(StandardCharsets.UTF_8));
                    }
                } catch (SQLException e) {
                respondBuffer.put("Data is ignored".getBytes(StandardCharsets.UTF_8));
                Controller.getInstance().showErrorMessage("Could not check if user exists in database");
                } finally {
                respondBuffer.flip();
                    try {
                        client.write(respondBuffer);
                    } catch (IOException e) {
                        Controller.getInstance().showErrorMessage("Could not send respond client\ndata status");
                    }
                }
        }
        else if (clientData.startsWith("NeedJson\n")) {
            ObserverData observer = ParseJSON.HandleRequest(clientData);
            //TODO handle requests
            if (observer == null) {
                sendErrorRespond(client);
                return;
            }

            DBManager manager = new DBManager();
            int privilege = 0;
            try {
                privilege = manager.getPrivilege(observer.getName());
            } catch (SQLException e) {
                Controller.getInstance().showErrorMessage("Could not get user's privilege");
                sendErrorRespond(client);
                return;
            }
            if (privilege != 1) {
                sendErrorRespond(client);
                return;
            }
            boolean isObserverValid = true;
            try {
                isObserverValid = manager.isUserValid(observer.getName(), observer.getPassword());
                //TODO - delete this when password encryption is done
                isObserverValid = true;
            } catch (SQLException e) {
                Controller.getInstance().showErrorMessage("Could not verify observer's \nname and password");
                sendErrorRespond(client);
                return;
            }
            if (!isObserverValid) {
                sendErrorRespond(client);
                return;
            }
            ArrayList<User> usersList = new ArrayList<>();
            for (String userName : observer.getUsers()) {
                try {
                    usersList.add(manager.getUserWithPrograms(userName, observer.getPrograms(), observer.getFrom(), observer.getTo()));
                } catch(SQLException e) {
                    Controller.getInstance().showErrorMessage("Could not get info requested by observer");
                }
            }
            Gson gson = new Gson();
            String jsonString = gson.toJson(usersList);
            try {
                client.write(respondBuffer);
            } catch (IOException e) {
                Controller.getInstance().showErrorMessage("Could not send respond observer\n");
            }
        }
        else if (clientData.startsWith("Register client sender\n")) {
            //Respond register status
            if (ParseJSON.RegisterClSender(clientData)) {
                respondBuffer.put("Register successful".getBytes(StandardCharsets.UTF_8));
            } else {
                respondBuffer.put("Register failed".getBytes(StandardCharsets.UTF_8));
            }
        respondBuffer.flip();
            try {
                client.write(respondBuffer);
            } catch (IOException e) {
                Controller.getInstance().showErrorMessage("Could not send respond client\nregister status");
            }
        }
        else if (clientData.startsWith("Initialize observer\n")) {
            clientData = clientData.substring(20);

            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
                @Override
                public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    return Base64.getDecoder().decode(json.getAsString());
                }
            });
            gsonBuilder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
                @Override
                public JsonElement serialize(byte[] bytes, Type type, JsonSerializationContext jsonSerializationContext) {
                    //System.out.println(bytesToHex(bytes));
                    return new JsonPrimitive(Base64.getEncoder().encodeToString(bytes));
                }
            });
            Gson gson = gsonBuilder.create();

            LoginPasswordWrapper LPWrapper = null;

            LoginPasswordWrapper LPWrapper2 = new LoginPasswordWrapper();
            LPWrapper2.setName("");
            LPWrapper2.setPassword(new byte[16]);
            String json = gson.toJson(LPWrapper2);

            try {
                LPWrapper = gson.fromJson(clientData, LoginPasswordWrapper.class);
            } catch (JsonSyntaxException e) {
                Controller.getInstance().showErrorMessage("Error parsing JSon from observer");
                sendErrorRespond(client);
                return;
            }

            DBManager manager = new DBManager();
            boolean isObserverValid = false;
            try {
                isObserverValid = manager.isUserValid(LPWrapper.getName(), LPWrapper.getPassword()) && manager.getPrivilege(LPWrapper.getName()) == 1;
            } catch (SQLException e) {
                Controller.getInstance().showErrorMessage("Could not verify observer");
                sendErrorRespond(client);
                return;
            }

            String respond;
            if (!isObserverValid) {
                respond = gson.toJson(new UserProgramNamesWrapper(1, 0, null, null));
            }
            else {
                try {
                    respond = gson.toJson(new UserProgramNamesWrapper(1, 1, manager.getAllUserNames(), manager.getAllProgramNames()));
                } catch (SQLException e) {
                    Controller.getInstance().showErrorMessage("Error getting users or programs by observer's request");
                    sendErrorRespond(client);
                    return;
                }
            }
            respondBuffer.put(respond.getBytes(StandardCharsets.UTF_8));
            respondBuffer.flip();
            try {
                client.write(respondBuffer);
            } catch (IOException e) {
                Controller.getInstance().showErrorMessage("Could not send respond to server");
                sendErrorRespond(client);
                return;
            }
        }
    }

    public void sendErrorRespond(SocketChannel client) {
        ByteBuffer respondBuffer = ByteBuffer.allocate(1024 * 10);
        respondBuffer.put("Error".getBytes(StandardCharsets.UTF_8));
        respondBuffer.flip();
        try {
            client.write(respondBuffer);
        } catch (IOException e) {
            Controller.getInstance().showErrorMessage("Could not send respond observer\nregister status");
        }
    }
}
