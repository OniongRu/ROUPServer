import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
public class RequestHandler extends Thread {

    private int operation;
    private SocketChannel client;
    public RequestHandler(SelectionKey key, int operation)
    {
        this.operation = operation;
        client = (SocketChannel) key.channel();
        start();
    }
    public void run()
    {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            client.read(buffer);
            String text = new String(buffer.array(),"UTF-8").trim();
            if(text.length()>0) {
                System.out.println(currentThread().getId() + client.getRemoteAddress().toString() + " | data: |" + text + "|");
            }
            String end_msg = new String("EndThisConnection".getBytes(),"UTF-8").trim();
            if(text.equals(end_msg)) {
                try {
                    System.out.println(client.getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + currentThread().getId() + " ");
                    client.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if(text.equals("ClientCppMsg#4"))
            {
                String msg = "t"+currentThread().getId()+"_end"+client+"#";
                ByteBuffer buffCpp = ByteBuffer.allocate(1024);
                buffCpp.put(msg.getBytes());
                buffCpp.flip();
                client.write(buffCpp);
                try {
                    System.out.println(client.getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + currentThread().getId() + " ");
                    client.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }catch (IOException e) {
            try {
                System.out.println(client.getRemoteAddress()+" #DISCONNECTED# from thread"+currentThread().getId()+" ");
                client.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
