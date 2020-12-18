package dataSend;

import java.util.Set;

public class UserProgramNamesWrapper {
    private int OpType;
    private int accept;
    private Set<String> users;
    private Set<String> programs;
    public UserProgramNamesWrapper(int OpType, int accept, Set<String> users, Set<String> programs) {
        this.OpType = OpType;
        this.accept = accept;
        this.users = users;
        this.programs = programs;
    }
}
