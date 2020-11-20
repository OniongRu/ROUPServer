package databaseInteract;

import dataRecieve.DataPack;
import dataRecieve.ProgramClass;
import java.util.ArrayList;
import java.util.Queue;

public class DataPackToUser {
    Queue<DataPack> dataPacks;
    ArrayList<User> users;

    public DataPackToUser(Queue<DataPack> dataPacks, ArrayList<User> users) {
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

    public ArrayList<User> getUsers() { return users; }

    private void AddToUsers(DataPack dp) {
        User someUser = isUserInArray(dp.getUserName());
        if (someUser == null) {
            //ERROR(THIS IS IMPOSSIBLE)//Users Should be created when register(not sending dataPack)
            // ↓this is example to debug
            users.add(new User(dp.getUserName(), "somePas", new ArrayList<ProgramTracker>()));
            someUser = users.get(users.size() - 1);
        }

        System.out.println("Collect interval: " + dp.getCollectInterval());

        for (ProgramClass programClass : dp.getPrograms()) {
            someUser.addInfoAboutPrograms(dp.getDate(), dp.getActiveWindowProcessName(), dp.getCollectInterval(), programClass);
        }
        someUser.setID(10);
    }

    private User isUserInArray(String userName) {
        for (User user : users) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

}