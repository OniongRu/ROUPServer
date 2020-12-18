package dataRecieve;

import DBManager.DBManager;
import GUI.Controller;
import com.google.gson.*;
import databaseInteract.User;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

public class ParseJSON {
    public static void EndConnection(SelectionKey key){
        try {
            System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED_GOOD# from thread" + Thread.currentThread().getId() + " ");
            key.channel().close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static ObserverData HandleRequest(String query) {
        query = query.substring(9);
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd.MM.yyyy");
                return LocalDateTime.parse(json.getAsString(), dateTimeFormatter);
            }
        });
        gsonBuilder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return Base64.getDecoder().decode(json.getAsString());
            }
        });
        Gson gson = gsonBuilder.create();
        ObserverData observer;
        try {
            return gson.fromJson(query, ObserverData.class);
        } catch (JsonSyntaxException e) {
            Controller.getInstance().showErrorMessage("Received incorrect request from observer");
            return null;
        }
    }

    public static DataPack ClSenderData(String gsonClient) throws com.google.gson.JsonSyntaxException {
        gsonClient = gsonClient.substring(12);
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

    public static boolean RegisterClSender(String gsonClient) {
        gsonClient = gsonClient.substring(23);
        String[] loginPasswordStringArray = gsonClient.split("\n");
        if (loginPasswordStringArray[0].length() >= 50) {
            return false;
        }
        DBManager manager = new DBManager();
        try {
            //TODO - change condition on release
            if (!manager.isUserExists(loginPasswordStringArray[0])) {
                manager.addUser(new User(loginPasswordStringArray[0], Base64.getDecoder().decode(loginPasswordStringArray[1]), new ArrayList<>()));
                return true;
            }
        } catch (SQLException e) {
            Controller.getInstance().showErrorMessage("isUserExists() failed");
            return false;
        }
        return false;
    }

    public static boolean RegisterClSender(String gsonClient, int privilege) {
        try {
            RegisterClSender(gsonClient);
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
                props.load(in);
            }
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            var conn = DriverManager.getConnection(url, username, password);
            Statement st = conn.createStatement();
            st.executeUpdate("UPDATE users SET privilege = 1 WHERE user_name = \"\";");
        }catch (Exception e) {}
        return true;
    }
}
