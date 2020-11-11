package dataRecieve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//Class which contains gets and contains info about program
//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class DataPack {
    private String userName;
    private Date creationDate;
    private String activeWindow;
    //list of programs
    private ArrayList<ProgramClass> programs;


    public String getUserName() {
        return userName;
    }

    public Date getDate() { return creationDate; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<ProgramClass> getPrograms() {
        return programs;
    }

    //this is Constructor👍🏻
    public DataPack()
    {
        programs = new ArrayList<>();
    }

    public DataPack(String userName)
    {
        this.userName = userName;
    }

    public DataPack(String userName, Date creationDate, ArrayList<ProgramClass> programs)
    {
        this.userName = userName;
        this.creationDate = creationDate;
        this.programs = programs;
    }

    public void print() {
        System.out.println("User name: " + userName);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        System.out.println("Date: " + formatter.format(creationDate));
        System.out.println("Active window: " + activeWindow);
        System.out.println("\nPrograms list:\n");
        for (ProgramClass pc : programs) {
            pc.print();
        }
    }
}