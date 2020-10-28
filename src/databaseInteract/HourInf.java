package databaseInteract;

import dataRecieve.DataPack;
import dataRecieve.ProgramClass;
import databaseInteract.ResourceUsage;

import java.util.Date;

//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class HourInf {
    //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private int dataPackCount;
    private int timeSum;
    private int timeActSum;
    ResourceUsage resource;

    public int getTimeSum() {
        return timeSum;
    }

    public int getTimeActSum(){
        return timeActSum;
    }

    public ResourceUsage getResource() {
        return resource;
    }



    public HourInf(int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage)
    {

        this.timeSum = timeSum;
        this.timeActSum = timeActSum;
        this.resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
        this.dataPackCount=0;
    }
    public HourInf()
    {
        this.timeSum=0;
        this.timeActSum=0;
        this.dataPackCount=0;
        this.resource=new ResourceUsage();
    }


    public void AddNewProgram(ProgramClass programClass){
        dataPackCount++;
        resource.AddMoreInfoAbout(programClass.getThreadAmount(),programClass.getCpuUsage(),programClass.getRamUsage());
    }




}
