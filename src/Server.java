import dataRecieve.ClientGroup;
import databaseInteract.*;
import dataRecieve.*;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

//класс сервер принимает входящие соединения и назначает
//их на селектор в объекты класса dataRecieve.ClientGroup (1 объект = 1 поток)
public class Server {
    private static final int PORT = 5020;
    public static ArrayList<ClientGroup> serverList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.out.println("♂Server start♂");
            ServerSocketChannel ServerS = ServerSocketChannel.open();
            ServerS.bind(new InetSocketAddress(PORT));
            SocketChannel ClientS1 = ServerS.accept(); // ожидание входящего соединения
            serverList.add(new ClientGroup(ClientS1, PORT)); //инициализаия 1ого объекта
            ClientS1 = ServerS.accept(); // ожидание входящего соединения 
            serverList.add(new ClientGroup(ClientS1, PORT)); //инициализаия 2ого объекта
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
