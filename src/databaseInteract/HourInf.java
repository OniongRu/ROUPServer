package databaseInteract;


import dataRecieve.ProgramClass;

import java.time.LocalDateTime;


//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class HourInf {
    //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    private LocalDateTime creationDate;
    private int dataPackCount;
    private int timeSum;
    private int timeActSum;
    private ResourceUsage resource;

    public int getTimeSum() {
        return timeSum;
    }

    public int getTimeActSum(){
        return timeActSum;
    }

    public void incrementTimeActSum(int collectInterval){ timeActSum += collectInterval; }

    public ResourceUsage getResource() {
        return resource;
    }

    public LocalDateTime getCreationDate() { return creationDate; }

    public int getDataPackCount(){
        return dataPackCount;
    }


    public void print(){
        System.out.println("Date: " + creationDate);
        System.out.println("Datapack count: " + dataPackCount);
        System.out.println("Time sum: " + timeSum);
        System.out.println("Time active sum: " + timeActSum);
        resource.print();
        System.out.println();
    }

    public HourInf(int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage, LocalDateTime creationDate)
    {

        this.timeSum = timeSum;
        this.creationDate = creationDate;
        this.timeActSum = timeActSum;
        this.resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
        this.dataPackCount = 0;
    }

    public HourInf(int threadAmount, double cpuUsage, long ramUsage, LocalDateTime creationDate)
    {

        this.timeSum = 0;
        this.creationDate = creationDate;
        this.timeActSum = 0;
        this.resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
        this.dataPackCount = 0;
    }

    public HourInf()
    {
        this.timeSum = 0;
        this.timeActSum = 0;
        this.dataPackCount = 0;
        this.resource = new ResourceUsage();

    }
    
    public HourInf(LocalDateTime date)
    {
        this.creationDate = date;
        this.timeSum = 0;
        this.timeActSum = 0;
        this.dataPackCount = 0;
        this.resource = new ResourceUsage();
    }

    public void AddNewProgram(int collectInterval, ProgramClass programClass) {
        dataPackCount++;
        timeSum += collectInterval;
        resource.AddMoreInfoAbout(programClass.getThreadAmount(), programClass.getCpuUsage(), programClass.getRamUsage());
    }

    public void mergeInfo4DB(HourInf hourInf) {
        int dataPackCountFromDB = hourInf.dataPackCount;
        this.timeSum += hourInf.timeSum;
        this.timeActSum += hourInf.timeActSum;
        ResourceUsage someResource = hourInf.resource;
        this.resource.merge4UpdateDB(someResource.getThreadAmount() * dataPackCountFromDB,
                someResource.getCpuUsage() * dataPackCountFromDB,
                someResource.getRamUsage() * dataPackCountFromDB,
                dataPackCount);
        this.dataPackCount = dataPackCount + dataPackCountFromDB;
        finalizeObservations();

    }

    public void finalizeObservations() {
        this.resource.finalizeObservations(dataPackCount);
    }
}
