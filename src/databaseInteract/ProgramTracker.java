package databaseInteract;

import DBManager.DBManager;
import dataRecieve.ProgramClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//In construction. Not working yet
public class ProgramTracker {
    private long ID;
    private String name;
    private ArrayList<HourInf> HourWork;

    public ArrayList<HourInf> getHourWork() {
        return HourWork;
    }

    public void addNewProgram(Date date, ProgramClass programClass, String activeWindow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        //date.setMinutes(0);
        //date.setSeconds(0);
        assert date.getMinutes() != 0 || date.getSeconds() != 0;
        HourInf someHour = isHourInArray(date);
        if(someHour == null) {
            HourWork.add(new HourInf(date));
            someHour = HourWork.get(HourWork.size()-1);
        }
        this.name = programClass.getName();
        this.ID = programClass.getID();
        someHour.AddNewProgram(programClass, activeWindow);
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
        this.HourWork = new ArrayList<>();
    }

    public ProgramTracker(long ID, String name, ArrayList<HourInf> hourInf) {
        this.ID = ID;
        this.name = name;
        this.HourWork = new ArrayList<>();
        this.HourWork = hourInf;
    }

    private HourInf isHourInArray(Date date) {
        for (HourInf hour : HourWork) {
            if (hour.getCreationDate().equals(date)) {
                return hour;
            }
        }
        return null;
    }

    public void normalizeHourInf(DBManager dbManager) throws SQLException {
        for (HourInf hourInf : HourWork) {
            hourInf.normalizeHourInf();
            dbManager.addResourceUsage(hourInf.getResource(), this.ID);
        }
    }
}
