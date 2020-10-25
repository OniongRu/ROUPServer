import java.util.ArrayList;

public class Program {
    private int ID;
    private String name;
    private ArrayList<HourInf> HourWork;
    public Program(int ID, String name, ArrayList<HourInf> HourWork)
    {
        this.ID = ID;
        this.name = name;
        this.HourWork = HourWork;
    }
}
