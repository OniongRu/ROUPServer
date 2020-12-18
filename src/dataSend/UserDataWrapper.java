package dataSend;

import databaseInteract.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserDataWrapper {
    @Observable
    private int accept;
    @Observable
    private int OpType;

    @Observable
    private ArrayList<User> users;

    public UserDataWrapper(int accept, int OpType, ArrayList<User> users) {
        this.accept = accept;
        this.OpType = OpType;
        this.users = users;
    }
}
