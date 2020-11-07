package threadManager;

import dataRecieve.ClientGroup;
import dataRecieve.DataPack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class ThreadController {
    ServerSocketChannel ServerS = null;
    public static ArrayList<ClientGroup> serverList = new ArrayList<>();
    private boolean isServerToggledOff = true;

    private Queue<DataPack> dataPackQueue = new Queue<DataPack>() {
        @Override
        public boolean add(DataPack dataPack) {
            return false;
        }

        @Override
        public boolean offer(DataPack dataPack) {
            return false;
        }

        @Override
        public DataPack remove() {
            return null;
        }

        @Override
        public DataPack poll() {
            return null;
        }

        @Override
        public DataPack element() {
            return null;
        }

        @Override
        public DataPack peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<DataPack> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends DataPack> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };

    public boolean getIsServerToggledOff(){
        return isServerToggledOff;
    }

    public void setIsServerToggledOff(boolean isServerToggledOff){
        this.isServerToggledOff = isServerToggledOff;
    }

    private int getNumFreeServer(){
        int minServerIndex = 0;
        int curServerIndex = 0;
        int minServerConn = serverList.get(0).getClientAm();
        for (ClientGroup server : serverList){
            int curServerConn = server.getClientAm();
            if (curServerConn < minServerConn){
                minServerIndex = curServerIndex;
                minServerConn = curServerConn;
            }
            curServerIndex++;
        }
        return minServerIndex;
    }

    public void launchService(final int PORT) throws IOException {
            ServerS = ServerSocketChannel.open();
            ServerS.bind(new InetSocketAddress(PORT));

            int coresNum = Runtime.getRuntime().availableProcessors();
            //Выделяем число потоков равное количеству ядер в системе под обработку информации от клиентов
            for (int curThNum = 0; curThNum < coresNum; curThNum++) {
                SocketChannel ClientS = ServerS.accept(); // ожидание входящего соединения
                serverList.add(new ClientGroup(ClientS, PORT, dataPackQueue)); //инициализаия 1ого объекта
            }

            while (!isServerToggledOff) {
                SocketChannel ClientS = ServerS.accept(); // ожидание входящего соединения
                ClientS.configureBlocking(false);
                serverList.get(getNumFreeServer()).AddSocket(ClientS); // регистрация входящего соединения в селекторе одного из объекта
            }
    }

    public void closeService() throws IOException {
        isServerToggledOff = true;
        for (ClientGroup server : serverList){
            server.interrupt();
        }

        ServerS.close();
        serverList.clear();
    }

}
