package GUI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Properties
{
    private static final String propertiesPath = "server.properties";

    private static Properties thisAppProperties = null;

    private String login = "GooseDefault";
    private static final int DEFAULTPORT = 5020;
    private int port = 5020;
    private String databaseConnection = "jdbc:mysql://localhost/test?serverTimezone=Europe/Moscow&useSSL=false";

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public static int getDEFAULTPORT()
    {
        return DEFAULTPORT;
    }

    public int getPort()
    {
        return port;
    }

    public String getDatabaseConnection()
    {
        return databaseConnection;
    }

    public Properties()
    {
        thisAppProperties = this;
    }

    public static Properties getInstance()
    {
        return thisAppProperties;
    }

    public static void serializeProperties()
    {
        //Make changes in case properties are changed while app was working
        Properties.getInstance().setLogin(Controller.getInstance().getLogin());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String properties = gson.toJson(Properties.getInstance());
        FileWriter propertiesFWriter;
        try
        {
            propertiesFWriter = new FileWriter(propertiesPath);
        } catch (IOException e)
        {
            Controller.getInstance().showStatusMessage("Could find path to update\nproperties file file");
            return;
        }
        try
        {
            propertiesFWriter.write(properties);
            propertiesFWriter.close();
        } catch (IOException e)
        {
            Controller.getInstance().showStatusMessage("Could not update\nproperties file");
            return;
        }
    }

    public static void deserializeProperties()
    {
        //Getting properties from file
        FileReader configFileReader = null;
        try
        {
            configFileReader = new FileReader(propertiesPath);
        } catch (FileNotFoundException e)
        {
            Controller.getInstance().showStatusMessage("Config file not found\nUsing default parameters");
            return;
        }
        Gson gson = new Gson();
        try
        {
            thisAppProperties = gson.fromJson(configFileReader, Properties.class);
            configFileReader.close();
        } catch (JsonSyntaxException | JsonIOException | IOException e)
        {
            Controller.getInstance().showStatusMessage("Could not read config file\nUsing default parameters");
            return;
        }
    }

    public boolean isPortValid()
    {
        int port = Properties.getInstance().getPort();
        if (port <= 1024 || port > 65535)
        {
            return false;
        }
        return true;
    }
}
