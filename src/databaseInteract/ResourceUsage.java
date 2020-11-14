package databaseInteract;

public class ResourceUsage {
    private int threadAmount;
    private double cpuUsage;
    private long ramUsage;
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


    public void AddMoreInfoAbout(int threadAmount, double cpuUsage, long ramUsage)
    {
        this.threadAmount += threadAmount;
        this.cpuUsage += cpuUsage;
        this.ramUsage += ramUsage;
    }

    public int get_threadAmount()
    {
        return threadAmount;
    }

    public double get_cpuUsage() {
        return cpuUsage;
    }

    public long get_ramUsage() {
        return ramUsage;
    }

    public void normalizeHourInf(int dataPackCount) {
        threadAmount /= dataPackCount;
        cpuUsage /= dataPackCount;
        ramUsage /= dataPackCount;
    }
}


