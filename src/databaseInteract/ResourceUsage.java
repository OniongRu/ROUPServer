package databaseInteract;

public class ResourceUsage {
    private int threadAmount;
    private double cpuUsage;
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

    public void merge4UpdateDB(int threadAmount, double cpuUsage, long ramUsage, int dbCount) {
        this.threadAmount = this.threadAmount * dbCount + threadAmount;
        this.cpuUsage = this.cpuUsage * dbCount + cpuUsage;
        this.ramUsage = this.ramUsage * dbCount + ramUsage;
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

}


