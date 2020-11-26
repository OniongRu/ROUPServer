package dataRecieve;

import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;

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
        //Gson gson = new Gson();
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
                return LocalDateTime.parse(json.getAsString(), formatter);
            }
        });
        gsonBuilder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return Base64.getDecoder().decode(json.getAsString());
            }
        });
        Gson gson = gsonBuilder.create();
        return gson.fromJson(gsonClient, DataPack.class);
    }
}
