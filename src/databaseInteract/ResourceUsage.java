package databaseInteract;

import dataSend.Observable;

public class ResourceUsage {
    @Observable
    private int threadAmount;

    @Observable
    private double cpuUsage;

    @Observable
    private long ramUsage;

    public void print(){
        System.out.println("Thread amount: " + threadAmount);
        System.out.println("CPU usage: " + cpuUsage);
        System.out.println("RAM usage: " + ramUsage);
    }

    public ResourceUsage(int threadAmount, double cpuUsage, long ramUsage)
    {
        this.threadAmount = threadAmount;
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
    }

    public ResourceUsage(){
        this.threadAmount = 0;
        this.cpuUsage = 0;
        this.ramUsage = 0;
    }

    public void AddMoreInfoAbout(int threadAmount, double cpuUsage, long ramUsage) {
        this.threadAmount += threadAmount;
        this.cpuUsage += cpuUsage;
        this.ramUsage += ramUsage;
    }

    public void mergeFinalizedResourceUsage(int threadAmount, double cpuUsage, long ramUsage, int dpCount, int dpCountFromDB) {
        this.threadAmount = this.threadAmount * dpCount + threadAmount * dpCountFromDB;
        this.cpuUsage = this.cpuUsage * dpCount + cpuUsage * dpCountFromDB;
        this.ramUsage = this.ramUsage * dpCount + ramUsage * dpCountFromDB;
    }

    public int getThreadAmount() {
        return threadAmount;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public long getRamUsage() {
        return ramUsage;
    }

    public void finalizeObservations(int dataPackCount) {
        this.threadAmount /= dataPackCount;
        this.ramUsage /= dataPackCount;
        this.cpuUsage /= dataPackCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof ResourceUsage)) {
            return false;
        }

        ResourceUsage obj = (ResourceUsage) o;

        return obj.ramUsage == this.ramUsage && obj.cpuUsage == this.cpuUsage && obj.threadAmount == this.threadAmount;
    }

    //For testing
    public static ResourceUsage aResourceUsage() {
        return new ResourceUsage();
    }

    public void withThreadAmount(int threadAmount) {
        this.threadAmount = threadAmount;
    }

    public void withCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public void withRamUsage(long ramUsage) {
        this.ramUsage = ramUsage;
    }
}


