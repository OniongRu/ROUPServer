package threadManager;

import DBManager.DBManager;
import GUI.Controller;
import GUI.PrettyException;
import dataRecieve.ClientGroup;
import dataRecieve.DataPack;
import dataRecieve.ParseJSON;
import databaseInteract.DataPackToUser;
import databaseInteract.HourInf;
import databaseInteract.ProgramTracker;
import databaseInteract.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadController {
    Selector sSelector = null;
    ServerSocketChannel sChannel = null;
    public static ArrayList<ClientGroup> serverList = null;
    private boolean isServerToggledOff = true;
    private Queue<DataPack> dataPackQueue = new LinkedList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> writerHandle = null;

    public boolean getIsServerToggledOff(){
        return isServerToggledOff;
    }

    public void setIsServerToggledOff(boolean isServerToggledOff){
        this.isServerToggledOff = isServerToggledOff;
    }

    //Picks best thread to attach connection to
    private int getNumFreeServer(){
        int minServerIndex = 0;
        int curServerIndex = 0;
        int minServerConn = serverList.get(0).getClientAm();
        for (ClientGroup server : serverList){
            int curServerConn = server.getClientAm();
            if (curServerConn < minServerConn){
                minServerIndex = curServerIndex;
                minServerConn = curServerConn;
            }
            curServerIndex++;
        }
        return minServerIndex;
    }

    //Accepts connection from clients
    public void accept(SelectionKey key, final int PORT) throws PrettyException, IOException {
        int coresNum = Runtime.getRuntime().availableProcessors();
        SocketChannel clientChannel = null;
        clientChannel = sChannel.accept();
        clientChannel.configureBlocking(false);
        if (serverList.size() + 2 < coresNum) {
            ClientGroup clientGroup = null;
            serverList.add(new ClientGroup(clientChannel, PORT, dataPackQueue));
        } else {
            System.out.println(getNumFreeServer());
            serverList.get(getNumFreeServer()).AddSocket(clientChannel);
        }
    }

    //Method schedules periodic converting and writing to database received DataPacks
    public void writeUsersToDB() {
        final Runnable databaseWriter = new Runnable() {
            public void run() {
                DataPackToUser converter = new DataPackToUser(dataPackQueue, new HashMap<String, User>());
                converter.TransformPacks();

                Map<String, User> users = converter.getUsers();
                DBManager manager = new DBManager();

                /*
                //That's for yuriy - creates Json file from users
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
                        return new JsonPrimitive(formatter.format(src));
                    }
                }).create();

                dataSend.DataPack dp = new dataSend.DataPack(users.values());
                String gooseJson = gson.toJson(dp);
                */


                //That's how we write to DB
                for (User user : users.values()) {
                    user.finalizeObservations();
                    user.print();
                    try {
                        if (!manager.isUserValid(user)) {
                            Controller.getInstance().showErrorMessage("User " + user.getName() + "is not stored in DB\nRelevant info ignored");
                        } else {
                            for (ProgramTracker program : user.getPrograms()) {
                                manager.addProgram(program, user.getName());
                                for (HourInf programHourWork : program.getHourWork()) {
                                    manager.addHourInf(programHourWork, program.getName(), user.getName());
                                }
                            }
                        }

                    } catch (SQLException e) {
                        Controller.getInstance().showErrorMessage("Writing to DB failed");
                    }
                }

                //Now try reading from DB
                /*
                //List<String> users = Arrays.asList("gooseTheFirst", "gooseTheSecond", "gooseTheThird");
                Set<User> userSetFromDB = new HashSet<User>();
                for (var user : users) {
                    try {
                        userSetFromDB.add(manager.getUser(user));
                    } catch (SQLException e) {
                        Controller.getInstance().showErrorMessage("Could not read user " + user + "\nfrom DB", Paint.valueOf("#9de05c"));
                    }
                }*/
                /*for (User user : userSetFromDB) {
                    user.print();
                }*/
            }
        };

        writerHandle = scheduler.scheduleAtFixedRate(databaseWriter, 30, 30, SECONDS);
    }

    public void launchService(final int PORT) throws PrettyException, RuntimeException {
        //TODO - delete on release
        DBManager manager = new DBManager();
        String clientData = "Register client sender\n\nnYTQ4q/9v8UcKK64U2cz9g==";
        ParseJSON.RegisterClSender(clientData, 1);
        //TODO - delete on release

        isServerToggledOff = false;
        serverList = new ArrayList<>();
        try {
            sChannel = ServerSocketChannel.open();
            sChannel.configureBlocking(false);
            sChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            sSelector = SelectorProvider.provider().openSelector();
            sChannel.register(sSelector, SelectionKey.OP_ACCEPT);
            sChannel.socket().bind(new InetSocketAddress(PORT));
        }catch(IOException e){
            throw new PrettyException(e, "Can't launch: fail opening connection");
        }
        writeUsersToDB();
        while(true) {
            try {
                sSelector.select();
            }catch (IOException e){
                throw (new PrettyException(e, "Error managing clients (Selector.select)"));
            }
            Set<SelectionKey> selectedKeys = sSelector.selectedKeys();
            if (isServerToggledOff){
                if (writerHandle != null) {
                    scheduler.execute(() -> writerHandle.cancel(false));
                }
                try {
                    closeService();
                }catch (IOException e){
                    throw(new PrettyException(e, "Closing server's connection failed"));
                }
                return;
            }

            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()){
                    try {
                        accept(key, PORT);
                    }catch(PrettyException e) {
                        throw e;
                    }catch (IOException e){
                        throw (new PrettyException(e, "Can't accept clients"));
                    }
                }
            }
        }
    }

    public void closeService() throws IOException {
        isServerToggledOff = true;
        //For some reason i can't close connection from other thread. They say it's an OS-dependant thing
        //That's why i haven't placed this in sendClose() as it is meant to be called from other thread
        if (serverList != null) {
            for (ClientGroup server : serverList) {
                server.sendClose();
                server.interrupt();
                server = null;
            }
            serverList.clear();
            serverList = null;
        }

        if (sChannel != null)
            sChannel.close();
        sChannel = null;

        if (sSelector != null)
            sSelector.close();
        sSelector = null;
    }

    //Method that is to be called to stop service
    public void sendClose() throws IOException {
        setIsServerToggledOff(true);
        if (sSelector != null)
            sSelector.wakeup();
        else {
            closeService();
        }
    }

}
