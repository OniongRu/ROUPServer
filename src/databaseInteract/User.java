package databaseInteract;

import dataRecieve.ProgramClass;

import java.util.ArrayList;
import java.util.Date;

//In construction. Not working yet
public class User {
    private String name;
    private String login;
    private String password;
    private ArrayList<Program> Programs;

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Program> getPrograms() {
        return Programs;
    }

    public User(String name, String login, String password, ArrayList<Program> Programs) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.Programs = Programs;
    }

    public void addInfoAboutPrograms(Date date,ProgramClass program){
        for(Program prog: Programs)
        {
            if(prog.getName().equals(program.getName())){
                prog.addNewProgram(date,program);
            }
        }

    }
}
