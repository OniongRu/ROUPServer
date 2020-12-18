package databaseInteract;

import dataRecieve.ProgramClass;
import dataSend.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

//In construction. Not working yet
public class User {
    @Observable
    private int ID;

    @Observable
    private String name;

    private byte[] password;


    //TODO - change arraylist to map
    @Observable
    private ArrayList<ProgramTracker> programs;

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        assert (password.length > 128);
        return password;
    }

    public ArrayList<ProgramTracker> getPrograms() {
        return programs;
    }

    public void setID(int id) { ID = id; }

    public int getID() { return ID; }

    public void print(){
        System.out.println("User ID: " + ID);
        System.out.println("User name: " + name);
        System.out.println("User password: " + password);
        System.out.println();
        for (ProgramTracker trackedProgram : programs){
            trackedProgram.print();
        }
        System.out.println("***********");
    }

    public User(int id, String name, byte[] password, ArrayList<ProgramTracker> programs) {
        this.ID = id;
        this.name = name;
        this.password = password;
        this.programs = programs;
    }

    public User(String name, byte[] password, ArrayList<ProgramTracker> programs) {
        this.name = name;
        this.password = password;
        this.programs = programs;
    }

    public void addInfoAboutPrograms(LocalDateTime date, String activeWindowProcessName, int collectInterval, ProgramClass program){
        for(ProgramTracker prog: programs)
        {
            if(prog.getName().equals(program.getName())){
                prog.addNewProgram(date, activeWindowProcessName, collectInterval, program);
                return;
            }
        }

        programs.add(new ProgramTracker(program.getID(), program.getName()));
        programs.get(programs.size() - 1).addNewProgram(date, activeWindowProcessName, collectInterval, program);
    }

    public void finalizeObservations() {
        for (ProgramTracker program : programs) {
            program.finalizeObservations();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof User)) {
            return false;
        }

        User obj = (User) o;

        if (obj.ID == this.ID && obj.programs.equals(this.programs) && obj.name == this.name && Arrays.equals(obj.password, this.password))
            return true;
        return false;
    }

    User() {
    }

    public static User aUser() {
        return new User();
    }

    public void withID(int ID) {
        this.ID = ID;
    }

    public void withName(String name) {
        this.name = name;
    }

    public void withPassword(byte[] password) {
        this.password = password;
    }

    public void withPrograms(ArrayList<ProgramTracker> programs) {
        this.programs = programs;
    }

    public boolean is(User other) {
        if (this.name.equals(other.name) && Arrays.equals(this.password, other.password) && this.ID == other.ID && this.programs.equals(other.programs)) {
            return true;
        }
        else
            return false;
    }
}
