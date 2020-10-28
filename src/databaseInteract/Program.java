package databaseInteract;


import dataRecieve.DataPack;
import dataRecieve.ProgramClass;

import java.util.Date;
import java.util.HashMap;

//In construction. Not working yet
public class Program {
    private long ID;
    private String name;
    private HashMap<Date,HourInf> HourWork;

    public HashMap<Date, HourInf> getHourWork() {        return HourWork;
    }

    public void addNewProgram(Date date, ProgramClass programClass)
    {
      date.setMinutes(0);
        date.setSeconds(0);
        if(!HourWork.containsKey(date)){
            HourWork.put(date,new HourInf());
        }
        this.name=programClass.getName();
        this.ID=programClass.getID();
        HourWork.get(date).AddNewProgram(programClass);
    }


    public long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }


    public Program(int ID, String name)
    {
        this.ID = ID;
        this.name = name;
        this.HourWork = new HashMap<>();
    }

}
