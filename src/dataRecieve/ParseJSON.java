package dataRecieve;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ParseJSON {
    public static void parseEndConnection(SelectionKey key){
        try {
            System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + Thread.currentThread().getId() + " ");
            key.channel().close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static DataPack parseClSender(SelectionKey key, String gsonClient) throws com.google.gson.JsonSyntaxException {
        gsonClient = gsonClient.substring(12);
        Gson gson = new Gson();
        return gson.fromJson(gsonClient, DataPack.class);
    }
}
