package threadManager;

import GUI.PrettyException;
import dataRecieve.ClientGroup;
import dataRecieve.DataPack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class ThreadController {
    Selector sSelector = null;
    ServerSocketChannel sChannel = null;
    public static ArrayList<ClientGroup> serverList = null;
    private boolean isServerToggledOff = true;

    private Queue<DataPack> dataPackQueue = new Queue<DataPack>() {
        @Override
        public boolean add(DataPack dataPack) {
            return false;
        }

        @Override
        public boolean offer(DataPack dataPack) {
            return false;
        }

        @Override
        public DataPack remove() {
            return null;
        }

        @Override
        public DataPack poll() {
            return null;
        }

        @Override
        public DataPack element() {
            return null;
        }

        @Override
        public DataPack peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<DataPack> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends DataPack> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };

    public ThreadController() {
    }

    public boolean getIsServerToggledOff(){
        return isServerToggledOff;
    }

    public void setIsServerToggledOff(boolean isServerToggledOff){
        this.isServerToggledOff = isServerToggledOff;
    }

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

    public void launchService(final int PORT) throws PrettyException, RuntimeException{
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

        while(true) {
            try {
                sSelector.select();
            }catch (IOException e){
                throw (new PrettyException(e, "Error managing clients (Selector.select)"));
            }
            Set<SelectionKey> selectedKeys = sSelector.selectedKeys();
            if (isServerToggledOff){
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
        //That's why i haven't placed this in closeService() as meant to be called from main thread
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

    public void sendClose() throws IOException {
        isServerToggledOff = true;
        if (sSelector != null)
            sSelector.wakeup();
        else {
            closeService();
        }
    }

}
