package databaseInteract;

import dataRecieve.DataPack;
import dataRecieve.ProgramClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class DPToBD {
    Queue<DataPack> dataPacks;
    HashMap<String,User> users=new HashMap<>();
    public DPToBD(Queue<DataPack> dataPacks)
    {
        this.dataPacks=dataPacks;
    }

    public void TransformPacks(){
            while (!dataPacks.isEmpty())
            {
                DataPack dp= dataPacks.peek();
                dataPacks.remove();
                AddToUsers(dp);
            }
        }


    private void AddToUsers(DataPack dp)
    {
        if(users.containsKey(dp.getUserName())){//Is exist the user of this DataPack in Users List
            //Почему рот в говне?
        }else{
            //ERROR(THIS IS IMPOSSIBLE)//Users Should be created when register(not sending dataPack)
            // ↓this is example to debug
            users.put(dp.getUserName(),new User(dp.getUserName(), "SomeLogin","somePas",new ArrayList<Program>()));


        }
        for(ProgramClass programClass: dp.getPrograms()) {
            users.get(dp.getUserName()).addInfoAboutPrograms(dp.getDate(),programClass);
        }

    }

}
