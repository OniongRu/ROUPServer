import java.io.*;
import java.net.*;
import java.lang.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


public class Server {
    private static final int PORT = 5020;
    public static ArrayList<ClientGroup> serverList = new ArrayList<>();
    public static void main(String[] args) {
        try{
            System.out.println("♂Server start♂");
            ServerSocketChannel ServerS = ServerSocketChannel.open();
            ServerS.bind(new InetSocketAddress(PORT));
            SocketChannel ClientS1 = ServerS.accept();
            serverList.add(new ClientGroup(ClientS1,PORT));
            ClientS1 = ServerS.accept();
            serverList.add(new ClientGroup(ClientS1,PORT));
            int ThreadVar=0;
            while(true)
            {
                SocketChannel ClientS = ServerS.accept();
                ClientS.configureBlocking(false);
                serverList.get(ThreadVar).AddSocket(ClientS);
                ThreadVar = (ThreadVar + 1) % 2;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
