import java.util.ArrayList;

//Class which contains gets and contains info about program
//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class DataPack {
    private String userName;

    //list of programs
    private ArrayList<ProgramClass> programs;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<ProgramClass> getPrograms() {
        return programs;
    }

    //this is Constructor👍🏻
    public DataPack()
    {
        programs = new ArrayList<>();
    }

    public void print() {
        System.out.println("Name: " + userName);
        for (ProgramClass pc : programs) {
            pc.print();
        }
    }
}
