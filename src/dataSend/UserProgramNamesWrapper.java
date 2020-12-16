package dataSend;

import java.util.ArrayList;

public class UserProgramNamesWrapper {
    private int OpType;
    private int accept;
    private ArrayList<String> users;
    private ArrayList<String> programs;
    public UserProgramNamesWrapper(int OpType, int accept, ArrayList<String> users, ArrayList<String> programs) {
        this.OpType = OpType;
        this.accept = accept;
        this.users = users;
        this.programs = programs;
    }
}
