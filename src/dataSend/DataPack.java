package dataSend;

import com.sun.webkit.BackForwardList;
import databaseInteract.User;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DataPack {
    List<User> usersList;

    public DataPack() {
        usersList = new LinkedList<User>();
    }

    public DataPack(Collection<User> usersCollection) {
        usersList = new LinkedList<User>();
        setUsersList(usersCollection);
    }

    public List<User> getUserString() {
        return usersList;
    }

    public void setUsersList(LinkedList<User> usersList) {
        this.usersList = usersList;
    }

    public void setUsersList(Collection<User> usersCollection) { this.usersList = (List<User>)usersCollection; }
}
