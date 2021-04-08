package dataRecieve;

import DBManager.DBManager;
import GUI.Controller;
import GUI.PrettyException;
import com.google.gson.*;
import dataSend.DataObservableExposeStrategy;
import dataSend.UserDataWrapper;
import dataSend.UserProgramNamesWrapper;
import databaseInteract.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

//класс объекта, работающего в отдельном потоке, взаимодействующий с Сокетами,
//записанными в его селектор
public class ClientGroup extends Thread
{
    private Selector SelectorS;
    int PORT;
    private int clientAm = 0;
    private Queue<DataPack> dataPackQueue;
    private boolean isCloseSent = false;

    private void incrementCnt()
    {
        clientAm++;
    }

    private void decrementCnt()
    {
        clientAm--;
    }

    private void resetCnt()
    {
        clientAm = 0;
    }

    public int getClientAm()
    {
        return clientAm;
    }

    //конструктор
    public ClientGroup(SocketChannel ServElSoc, int PORT, Queue<DataPack> dataPackQueue) throws PrettyException
    {
        try
        {
            SelectorS = Selector.open();
            ServElSoc.configureBlocking(false);
            ServElSoc.register(SelectorS, SelectionKey.OP_READ);
            System.out.println(ServElSoc.getRemoteAddress() + " ♫CONNECTED♫");
        } catch (IOException e)
        {
            throw new PrettyException(e, "Error creating ClientGroup");
        }
        this.PORT = PORT;
        resetCnt();
        incrementCnt();
        this.dataPackQueue = dataPackQueue;
        start();
    }

    //добавление нового сокета в селектор
    public void AddSocket(SocketChannel ClientS) throws IOException
    {
        ClientS.configureBlocking(false);
        ClientS.register(SelectorS, SelectionKey.OP_READ);
        System.out.println(ClientS.getRemoteAddress() + " ♫CONNECTED♫");
        incrementCnt();
        SelectorS.wakeup();
    }

    public void run()
    {
        int internalErrorsCounter = 0;
        try
        {
            isCloseSent = false;
            while (true)
            {

                try
                {
                    SelectorS.select(); //ожидание действий от клиентов
                } catch (IOException e)
                {
                    throw new PrettyException(e, "Error managing client group");
                }

                if (isCloseSent)
                {
                    try
                    {
                        closeClientGroup();
                    } catch (IOException e)
                    {
                        throw new PrettyException(e, "Error closing client group");
                    }
                    return;
                }

                Set<SelectionKey> selectedKeys = SelectorS.selectedKeys(); //создание ключей для соединений, от которых пришли запросы
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext())
                {
                    SelectionKey key = iter.next();
                    if (key.isReadable())
                    {
                        if (!takeJson(key))
                        {
                            internalErrorsCounter++;
                            //TODO - react somehow on internal server errors
                        }
                    }
                    iter.remove();
                }
            }
        } catch (PrettyException e)
        {
            throw new RuntimeException(e.getPrettyMessage());
        }
    }

    public void closeClientGroup() throws IOException
    {
        if (SelectorS != null)
        {
            SelectorS.close();
        }
        SelectorS = null;
        if (dataPackQueue != null)
            dataPackQueue.clear();
        dataPackQueue = null;
    }

    public void sendClose()
    {
        isCloseSent = true;
        SelectorS.wakeup();
    }

    /*//TODO - delete this when debug not needed
    public byte[] getPBKDF2SecurePassword(String userName, String password) {
        try {
            byte[] salt;
            salt = "defaultPassword".getBytes();
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Controller.getInstance().showErrorMessage("Can't encrypt password:\nAlgorithm or KeySpec exception");
            Controller.getInstance().onTurnedOff();
            return null;
        } catch (Exception e) {
            Controller.getInstance().showErrorMessage("Can't encrypt password\nUnknown reason");
            Controller.getInstance().onTurnedOff();
            return null;
        }
    }*/

    //приём json-а от клиента и преобразование в объекта dataReceive.DataPack
    //In this application there is an agreement "EndThisConnection" in a beginning means stop signal
    //Beginning "Client data\n" means that server is about to receive DataPack
    //Beginning "Request\n" means that server is about to receive a query for an administrator's client
    //Return value shows if exceptions occurred due to server or it's environment errors (value = false)
    //Or program worked correctly / received message is corrupted (value = true)
    private boolean takeJson(SelectionKey key)
    {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer requestBuffer = ByteBuffer.allocate(264000);
        requestBuffer.clear();
        try
        {
            client.read(requestBuffer);
        } catch (IOException e)
        {
            decrementCnt();
            try
            {
                System.out.println(((SocketChannel) key.channel()).getRemoteAddress() + " #DISCONNECTED# from thread" + currentThread().getId() + " ");
                key.channel().close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }

        String clientData = new String(requestBuffer.array()).trim();
        requestBuffer.clear();

        //TODO - delete after debug
        /*System.out.println(clientData);
        System.out.println(clientData.length());
        return true;*/

        ByteBuffer respondBuffer = ByteBuffer.allocate(1024 * 10);

        if (clientData.equals("EndThisConnection"))
        {
            decrementCnt();
            ParseJSON.EndConnection(key);
        } else if (clientData.startsWith("Client data\n"))
        {
            System.out.println(clientData);
            DataPack dataPackFromUser = ParseJSON.ClSenderData(clientData);
            DBManager manager = null;
            try
            {
                manager = new DBManager();
            }
            catch(Exception e)
            {
                Controller.getInstance().showStatusMessage("DB connection failed. Data ignored");
                return false;
            }
            try
            {
                if (manager.isUserValid(dataPackFromUser.getUserName(), dataPackFromUser.getPassword()))
                {
                    dataPackQueue.add(dataPackFromUser);
                    respondBuffer.put("Data is being processed".getBytes(StandardCharsets.UTF_8));
                } else
                {
                    //Login and/or password incorrect. No exceptions thrown.
                    respondBuffer.put("Data is ignored".getBytes(StandardCharsets.UTF_8));
                }
            } catch (SQLException e)
            {
                Controller.getInstance().showStatusMessage("Could not check if user exists in database");
                return false;
            }
            respondBuffer.flip();
            try
            {
                client.write(respondBuffer);
            }
            catch (IOException e)
            {
                Controller.getInstance().showStatusMessage("Could not send respond client's data status");
                return false;
            }
        } else if (clientData.startsWith("NeedJson\n"))
        {
            ObserverData observer = ParseJSON.HandleRequest(clientData);
            if (observer == null)
            {
                //Received message is incorrect
                sendErrorRespond(client);
                return true;
            }

            DBManager manager = null;
            try
            {
                manager = new DBManager();
            }
            catch(Exception e)
            {
                Controller.getInstance().showStatusMessage("DB connection failed. Register failed.");
                sendErrorRespond(client);
                return false;
            }

            int isObserverValid = 0;
            try
            {
                isObserverValid = manager.isUserValid(observer.getName(), observer.getPassword()) ? 1 : 0;
            } catch (SQLException e)
            {
                Controller.getInstance().showStatusMessage("Could not verify observer's \nname and password");
                sendErrorRespond(client);
                return false;
            }

            int privilege = 0;
            try
            {
                privilege = manager.getPrivilege(observer.getName());
            } catch (SQLException e)
            {
                Controller.getInstance().showStatusMessage("Could not get user's privilege");
                sendErrorRespond(client);
                return false;
            }
            if (privilege != 1)
            {
                //User with wrong privilege requested data
                sendErrorRespond(client);
                return true;
            }

            ArrayList<User> usersList = new ArrayList<>();
            for (String userName : observer.getUsers())
            {
                try
                {
                    usersList.add(manager.getUserWithPrograms(userName, observer.getPrograms(), observer.getFrom(), observer.getTo()));
                } catch (SQLException e)
                {
                    Controller.getInstance().showStatusMessage("Could not get info requested by observer");
                    return false;
                }
            }

            GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new DataObservableExposeStrategy());
            gsonBuilder.registerTypeAdapter(
                    LocalDateTime.class, new JsonSerializer<LocalDateTime>()
                    {
                        @Override
                        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context)
                        {
                            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
                            return new JsonPrimitive(formatter.format(src));
                        }
                    });
            Gson gson = gsonBuilder.create();
            String jsonString = gson.toJson(new UserDataWrapper(isObserverValid, 1, usersList));
            respondBuffer.put(jsonString.getBytes(StandardCharsets.UTF_8));
            respondBuffer.flip();
            try
            {
                client.write(respondBuffer);
            } catch (IOException e)
            {
                Controller.getInstance().showStatusMessage("Could not send respond observer\n");
                return false;
            }
        } else if (clientData.startsWith("Register client sender\n"))
        {
            //Respond register status
            try
            {
                if (ParseJSON.RegisterClSender(clientData))
                {
                    respondBuffer.put("Register successful".getBytes(StandardCharsets.UTF_8));
                } else
                {
                    //Login and/or password incorrect
                    respondBuffer.put("Register failed".getBytes(StandardCharsets.UTF_8));
                }
            }
            catch(Exception e)
            {
                Controller.getInstance().showStatusMessage("DB connection failed. Register failed.");
                return false;
            }

            respondBuffer.flip();
            try
            {
                client.write(respondBuffer);
            } catch (IOException e)
            {
                Controller.getInstance().showStatusMessage("Couldn't confirm registration");
                return false;
            }
        } else if (clientData.startsWith("Initialize observer\n"))
        {
            clientData = clientData.substring(20);

            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>()
            {
                @Override
                public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
                {
                    return Base64.getDecoder().decode(json.getAsString());
                }
            });
            gsonBuilder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>()
            {
                @Override
                public JsonElement serialize(byte[] bytes, Type type, JsonSerializationContext jsonSerializationContext)
                {
                    //System.out.println(bytesToHex(bytes));
                    return new JsonPrimitive(Base64.getEncoder().encodeToString(bytes));
                }
            });
            Gson gson = gsonBuilder.create();

            LoginPasswordWrapper LPWrapper = null;

            try
            {
                LPWrapper = gson.fromJson(clientData, LoginPasswordWrapper.class);
            } catch (JsonSyntaxException e)
            {
                //Received message is incorrect
                Controller.getInstance().showStatusMessage("Error parsing JSon from observer");
                sendErrorRespond(client);
                return true;
            }

            DBManager manager = null;
            try
            {
                manager = new DBManager();
            }
            catch(Exception e)
            {
                Controller.getInstance().showStatusMessage("DB connection failed. Register observer failed.");
                return false;
            }

            boolean isObserverValid = false;
            try
            {
                isObserverValid = manager.isUserValid(LPWrapper.getName(), LPWrapper.getPassword()) && manager.getPrivilege(LPWrapper.getName()) == 1;
            } catch (SQLException e)
            {
                Controller.getInstance().showStatusMessage("Could not verify observer");
                sendErrorRespond(client);
                return false;
            }

            String respond;
            if (!isObserverValid)
            {
                respond = gson.toJson(new UserProgramNamesWrapper(0, 0, null, null));
            } else
            {
                try
                {
                    respond = gson.toJson(new UserProgramNamesWrapper(0, 1, manager.getAllUserNames(), manager.getAllProgramNames()));
                } catch (SQLException e)
                {
                    Controller.getInstance().showStatusMessage("Error getting users or programs by observer's request");
                    sendErrorRespond(client);
                    return false;
                }
            }
            respondBuffer.put(respond.getBytes(StandardCharsets.UTF_8));
            respondBuffer.flip();
            try
            {
                client.write(respondBuffer);
            } catch (IOException e)
            {
                Controller.getInstance().showStatusMessage("Could not send respond to server");
                sendErrorRespond(client);
                return false;
            }
        }
        return true;
    }

    public void sendErrorRespond(SocketChannel client)
    {
        ByteBuffer respondBuffer = ByteBuffer.allocate(1024 * 10);
        respondBuffer.put("Error".getBytes(StandardCharsets.UTF_8));
        respondBuffer.flip();
        try
        {
            client.write(respondBuffer);
        } catch (IOException e)
        {
            Controller.getInstance().showStatusMessage("Could not send error respond");
        }
    }
}
