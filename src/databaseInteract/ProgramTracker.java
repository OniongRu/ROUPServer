package databaseInteract;

import dataRecieve.ProgramClass;
import dataSend.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

//In construction. Not working yet
public class ProgramTracker {
    @Observable
    private long ID;

    @Observable
    private String name;

    @Observable
    private ArrayList<HourInf> hourWork;

    //TODO - change array list to set here for a quicker access to a specified hour. Maybe do the same with programTracker in users
    public ArrayList<HourInf> getHourWork() {  return hourWork;   }

    public void print(){
        System.out.println("Program ID: " + ID);
        System.out.println("Program name: " + name);
        for (HourInf hourProgramInfo : hourWork){
            hourProgramInfo.print();
        }
        System.out.println("---------");
    }

    public void addNewProgram(LocalDateTime date, String activeWindowProcessName, int collectInterval, ProgramClass programClass)
    {
        Calendar calendar = Calendar.getInstance();
        date = date.minusMinutes(date.getMinute());
        date = date.minusSeconds(date.getSecond());
        HourInf someHour = isHourInArray(date);
        if(someHour == null) {
            hourWork.add(new HourInf(date));
            someHour = hourWork.get(hourWork.size() - 1);
        }
        this.name = programClass.getName();
        this.ID = programClass.getID();
        someHour.AddNewProgram(collectInterval, programClass);
        if (activeWindowProcessName.equals(name)) {
            someHour.incrementTimeActSum(collectInterval);
        }
    }


    public long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public ProgramTracker(long ID, String name)
    {
        this.ID = ID;
        this.name = name;
        this.hourWork = new ArrayList<>();
    }

    public ProgramTracker(long ID, String name, ArrayList<HourInf> hourInf) {
        this.ID = ID;
        this.name = name;
        this.hourWork = hourInf;
    }

    private HourInf isHourInArray(LocalDateTime date)
    {
        for (HourInf hour: hourWork) {
            if(hour.getCreationDate().equals(date)) {
                return hour;
            }
        }
        return null;
    }

    public void finalizeObservations() {
        for (HourInf programHourInfo : hourWork){
            programHourInfo.finalizeObservations();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof ProgramTracker)) {
            return false;
        }

        ProgramTracker obj = (ProgramTracker) o;

        if (obj.ID == this.ID && obj.hourWork.equals(this.hourWork) && obj.name == this.name)
            return true;
        return false;
    }

    ProgramTracker() {
    }

    public static ProgramTracker aProgramTracker() {
        return new ProgramTracker();
    }

    public void withID(long ID) {
        this.ID = ID;
    }

    public void withName(String name) {
        this.name = name;
    }

    public void withHourWork(ArrayList<HourInf> hourWork) {
        this.hourWork = hourWork;
    }
}
