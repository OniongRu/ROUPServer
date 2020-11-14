package databaseInteract;

import DBManager.DBManager;
import dataRecieve.DataPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

public class DatabaseWriter extends Thread {

    ArrayList<User> users;
    Queue<DataPack> dataPacks;
    DataPackToUser dataPackToUser;
    DBManager dbManager;


    public DatabaseWriter(ArrayList<User> users, Queue<DataPack> dataPacks) {
        this.users = users;
        this.dataPacks = dataPacks;
        this.dataPackToUser = new DataPackToUser(dataPacks, users);
        this.dbManager = new DBManager();
    }

    public void run() {
        while (true) {
            dataPackToUser.TransformPacks();
            try {
                addToDB();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void addToDB() throws SQLException {
        Iterator<User> userIterator = users.iterator();
        while (userIterator.hasNext()) {
            User user = userIterator.next();
            user.normalizeHourInf(dbManager);
            dbManager.addUser(user);


            //TODO в дб манагере нет вложений(( ДОДЕЛАТЬ//Мб доделано


        }
    }


}
