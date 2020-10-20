import com.google.gson.Gson;

import java.io.*;
import java.nio.*;
import java.lang.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientGroup extends Thread{
    public Selector SelectorS;
    int PORT;
    public ClientGroup(SocketChannel ServElSoc, int PORT) throws IOException {
        SelectorS = Selector.open();
        ServElSoc.configureBlocking(false);
        ServElSoc.register(SelectorS, SelectionKey.OP_READ);
        System.out.println(ServElSoc.getRemoteAddress()+" ♫CONNECTED♫");
        this.PORT = PORT;
        start();
    }
    public void AddSocket(SocketChannel ClientS) throws IOException {
        ClientS.configureBlocking(false);
        ClientS.register(SelectorS, SelectionKey.OP_READ);
        System.out.println(ClientS.getRemoteAddress()+" ♫CONNECTED♫");
    }
    public void run()
    {
        try {
            while (true) {
                int res = 0;
                res = SelectorS.select(100);
                if (res > 0) {
                    Set<SelectionKey> selectedKeys = SelectorS.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        if (key.isReadable()) {
                            //showClientAnswer(key);
                            //RequestHandler ReadReq = new RequestHandler(key,0);

                            takeGson(key);
                        }
                        iter.remove();
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("♂PogCham Server is down♂");
        }
    }
    private static void takeGson(SelectionKey key)
    {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024*10);
            client.read(buffer);
            String gsonClient = new String(buffer.array()).trim();
            if(gsonClient.equals("EndThisConnection")) {
                try {
                    System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + currentThread().getId() + " ");
                    key.channel().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            else {
                Gson gson = new Gson();
                System.out.println(gsonClient);
                DataPack clientData = gson.fromJson(gsonClient, DataPack.class);
                clientData.print();
            }

        }catch (IOException e) {
            try {
                System.out.println(((SocketChannel)key.channel()).getRemoteAddress()+" #DISCONNECTED# from thread"+currentThread().getId()+" ");
                key.channel().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
/*
    private static void showClientAnswer(SelectionKey key) {
        try {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        String text = new String(buffer.array(),"UTF-8").trim();
        if(text.length()>0) {
            System.out.println(currentThread().getId() + client.getRemoteAddress().toString() + " | data: |" + text + "|");
        }
        String end_msg = new String("EndThisConnection".getBytes(),"UTF-8").trim();

        if(text.equals(end_msg)) {
            try {
                System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + currentThread().getId() + " ");
                key.channel().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
            if(text.equals("ClientCppMsg#4"))
            {
                String msg = "t"+currentThread().getId()+"_end"+(SocketChannel)key.channel()+"#";
                ByteBuffer buffCpp = ByteBuffer.allocate(1024);
                buffCpp.put(msg.getBytes());
                buffCpp.flip();
                client.write(buffCpp);
                try {
                    System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + currentThread().getId() + " ");
                    key.channel().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
    }catch (IOException e) {
            try {
                System.out.println(((SocketChannel)key.channel()).getRemoteAddress()+" #DISCONNECTED# from thread"+currentThread().getId()+" ");
                key.channel().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }*/
}
