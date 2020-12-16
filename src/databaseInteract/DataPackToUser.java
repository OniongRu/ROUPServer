package databaseInteract;

import dataRecieve.DataPack;
import dataRecieve.ProgramClass;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public class DataPackToUser {
    Queue<DataPack> dataPacks;
    Map<String, User> users;

    public DataPackToUser(Queue<DataPack> dataPacks, Map<String, User> users) {
        this.dataPacks = dataPacks;
        this.users = users;
    }

    public void TransformPacks() {
        while (!dataPacks.isEmpty()) {
            DataPack dp = dataPacks.peek();
            AddToUsers(dp);
            dataPacks.remove();
        }
    }

    public Map<String, User> getUsers() { return users; }

    private void AddToUsers(DataPack dp) {
        User someUser = users.get(dp.getUserName());
        if (someUser == null) {
            someUser = new User(dp.getUserName(), dp.getPassword(), new ArrayList<ProgramTracker>());
            users.putIfAbsent(dp.getUserName(), someUser);
        }

        for (ProgramClass programClass : dp.getPrograms()) {
            someUser.addInfoAboutPrograms(dp.getDate(), dp.getActiveWindowProcessName(), dp.getCollectInterval(), programClass);
        }
        someUser.setID(10);
    }
}