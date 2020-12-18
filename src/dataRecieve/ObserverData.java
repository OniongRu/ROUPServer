package dataRecieve;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ObserverData {
    private String name;
    private byte[] password;
    private ArrayList<String> users;
    private ArrayList<String> programs;
    private LocalDateTime from;
    private LocalDateTime to;

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public ArrayList<String> getPrograms() {
        return programs;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}
