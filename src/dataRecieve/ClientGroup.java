package dataRecieve;

import GUI.PrettyException;
import com.google.gson.Gson;

import java.io.*;
import java.nio.*;
import java.lang.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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

    //приём json-а от клиента и преобразование в объекта dataReceive.DataPack
    //In this application there is an agreement "EndThisConnection" in a beginning means stop signal
    //Beginning "Client data\n" means that server is about to receive DataPack
    //Beginning "Request\n" means that server is about to receive a query for an administrator's client
    private void takeGson(SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024*10);
            client.read(buffer);
            String gsonClient = new String(buffer.array()).trim();
            if(gsonClient.equals("EndThisConnection")) {//TODO Add types of getting data
                decrementCnt();
                ParseJSON.parseEndConnection(key);
            }
            else if (gsonClient.startsWith("Client data\n")) {
                dataPackQueue.add(ParseJSON.parseClSender(key, gsonClient));
                System.out.println(gsonClient);
            }
            else if (gsonClient.startsWith("Request\n")) {
                //TODO handle requests
            }
        }catch (IOException e) {
            decrementCnt();
            try {
                System.out.println(((SocketChannel)key.channel()).getRemoteAddress() + " #DISCONNECTED# from thread" + currentThread().getId() + " ");
                key.channel().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
