package databaseInteract;

import DBManager.DBManager;
import dataRecieve.ProgramClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

//In construction. Not working yet
public class User {
    private int ID;
    private String name;
    private String password;
    private ArrayList<ProgramTracker> Programs;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<ProgramTracker> getPrograms() {
        return Programs;
    }

    public void setID(int id) {ID=id;}

    public int getID() {return ID;}

    public User(int id,String name, String password, ArrayList<ProgramTracker> Programs) {
        this.ID = id;
        this.name = name;
        this.password = password;
        this.Programs = Programs;
    }

    public User(String name, String password, ArrayList<ProgramTracker> Programs) {
        this.name = name;
        this.password = password;
        this.Programs = Programs;
    }

    public void addInfoAboutPrograms(Date date, ProgramClass program, String activeWindow) {
        for (ProgramTracker prog : Programs) {
            if (prog.getName().equals(program.getName())) {
                prog.addNewProgram(date, program, activeWindow);
                return;
            }
        }

        Programs.add(new ProgramTracker(program.getID(), program.getName()));
        Programs.get(Programs.size() - 1).addNewProgram(date, program, activeWindow);
    }

    public void normalizeHourInf(DBManager dbManager) throws SQLException {
        for (ProgramTracker program : Programs) {
            program.normalizeHourInf(dbManager);
            dbManager.addProgram(program, this.ID);
        }
    }
}
