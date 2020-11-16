package databaseInteract;

import dataRecieve.ProgramClass;

import java.util.ArrayList;
import java.util.Date;

//In construction. Not working yet
public class User {
    //TODO - get ID from DB using login and password
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

    public void setID(int id) { ID = id; }

    public int getID() { return ID; }

    public void print(){
        System.out.println("User ID: " + ID);
        System.out.println("User name: " + name);
        System.out.println("User password: " + password);
        System.out.println();
        for (ProgramTracker trackedProgram : Programs){
            trackedProgram.print();
        }
        System.out.println("***********");
    }

    public User(int id, String name, String password, ArrayList<ProgramTracker> Programs) {
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

    public void addInfoAboutPrograms(Date date, String activeWindowProcessName, int collectInterval, ProgramClass program){
        for(ProgramTracker prog: Programs)
        {
            if(prog.getName().equals(program.getName())){
                prog.addNewProgram(date, activeWindowProcessName, collectInterval, program);
                return;
            }
        }

        Programs.add(new ProgramTracker(program.getID(), program.getName()));
        Programs.get(Programs.size() - 1).addNewProgram(date, activeWindowProcessName, collectInterval, program);
    }
}
