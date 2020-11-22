package databaseInteract;

<<<<<<< HEAD
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
=======

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DatabaseWriter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void beepForAnHour() {
        final Runnable beeper = new Runnable() {
            public void run() { System.out.println("beep"); }
        };
        final ScheduledFuture<?> writerHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() { writerHandle.cancel(true); }
        }, 60 * 60, SECONDS);
    }

}
>>>>>>> origin/Slumdog
