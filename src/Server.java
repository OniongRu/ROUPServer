import dataRecieve.ClientGroup;
import databaseInteract.*;
import dataRecieve.*;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

//класс сервер принимает входящие соединения и назначает
//их на селектор в объекты класса dataRecieve.ClientGroup (1 объект = 1 поток)
public class Server {
    private static final int PORT = 5020;
    public static ArrayList<ClientGroup> serverList = new ArrayList<>();
    private static Queue<DataPack> dataPackQueue=new Queue<DataPack>() {
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

    public static void main(String[] args) {
        try {
            System.out.println("♂Server start♂");
            ServerSocketChannel ServerS = ServerSocketChannel.open();
            ServerS.bind(new InetSocketAddress(PORT));
            SocketChannel ClientS1 = ServerS.accept(); // ожидание входящего соединения
            serverList.add(new ClientGroup(ClientS1, PORT,dataPackQueue)); //инициализаия 1ого объекта
            ClientS1 = ServerS.accept(); // ожидание входящего соединения 
            serverList.add(new ClientGroup(ClientS1, PORT,dataPackQueue)); //инициализаия 2ого объекта
            int ThreadVar = 0;
            while (true) {
                SocketChannel ClientS = ServerS.accept();
                ClientS.configureBlocking(false);
                serverList.get(ThreadVar).AddSocket(ClientS); // регистрация входящего соединения в селекторе одного из объекта 
                ThreadVar = (ThreadVar + 1) % 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
