package databaseInteract;

import java.util.ArrayList;

//In construction. Not working yet
public class Program {
    private int ID;
    private String name;
    private ArrayList<HourInf> HourWork;

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<HourInf> getHourWork() {
        return HourWork;
    }

    public Program(int ID, String name, ArrayList<HourInf> HourWork)
    {
        this.ID = ID;
        this.name = name;
        this.HourWork = HourWork;
    }
}
