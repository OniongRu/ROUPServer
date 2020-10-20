import java.util.ArrayList;

public class DataPack {//Class which contains gets and contains info about program
    public String userName;
    public ArrayList<ProgramClass>  programs;//list of programs

    public DataPack()//this is Constructor👍🏻
    {
        programs =new ArrayList<>();
    }
    public void print()
    {
        System.out.println("Name: "+userName);
        for (ProgramClass pc:programs ) {
            pc.print();
        }
    }
}
