package databaseInteract;


import dataRecieve.ProgramClass;
import dataSend.Observable;

import java.time.LocalDateTime;


//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class HourInf {
    @Observable
    private LocalDateTime creationDate;

    @Observable
    private int dataPackCount;

    @Observable
    private int timeSum;

    @Observable
    private int timeActSum;

    @Observable
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

    public HourInf(int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage, LocalDateTime creationDate, int dataPackCount)
    {
        this.timeSum = timeSum;
        this.creationDate = creationDate;
        this.timeActSum = timeActSum;
        this.resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
        this.dataPackCount = dataPackCount;
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

    public void mergeFinalizedHourInfo(HourInf hourInf) {
        int dataPackCountFromDB = hourInf.dataPackCount;
        this.timeSum += hourInf.timeSum;
        this.timeActSum += hourInf.timeActSum;
        ResourceUsage someResource = hourInf.resource;
        this.resource.mergeFinalizedResourceUsage(someResource.getThreadAmount(),
                someResource.getCpuUsage(),
                someResource.getRamUsage(),
                dataPackCount, dataPackCountFromDB);
        this.dataPackCount = dataPackCount + dataPackCountFromDB;
        finalizeObservations();
    }

    public void finalizeObservations() {
        this.resource.finalizeObservations(dataPackCount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof HourInf)) {
            return false;
        }

        HourInf obj = (HourInf) o;

        if (obj.resource.equals(this.resource) && obj.timeSum == this.timeSum && obj.dataPackCount == this.dataPackCount
                && obj.timeActSum == this.timeActSum && obj.creationDate.equals(this.creationDate))
            return true;
        return false;
    }

    public static HourInf aHourInf() {
        return new HourInf();
    }

    public void withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void withDataPackCount(int dataPackCount) {
        this.dataPackCount = dataPackCount;
    }

    public void withTimeSum(int timeSum) {
        this.timeSum = timeSum;
    }

    public void withActTimeSum(int timeActSum) {
        this.timeActSum = timeActSum;
    }

    public void withResourceUsage(ResourceUsage resource) {
        this.resource = resource;
    }
}
