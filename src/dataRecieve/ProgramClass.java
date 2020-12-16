package dataRecieve;

public class ProgramClass {//class which contains info about program
    private String name;
    private long ID;
    private int threadAmount;
    private double cpuUsage;
    private long ramUsage;

    public ProgramClass(String name, long ID, int threadAmount, double cpuUsage, long ramUsage) {
        this.name = name;
        this.ID = ID;
        this.threadAmount = threadAmount;
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
    }

    public String getName() {
        return name;
    }

    public long getID() {
        return ID;
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

    //Method to merge databaseInteract.ProgramTracker with equal names
    //Merged ID will be the minimum ID of processes with the same name
    public void merge(long ID, int threadAmount, double cpuUsage, long ramUsage) {
        if (this.ID > ID)
            this.ID = ID;
        this.threadAmount += threadAmount;
        this.cpuUsage += cpuUsage;
        this.ramUsage += ramUsage;
    }

    //For testing only
    public static ProgramClass aProgramClass() {
        return new ProgramClass("", 0, 0, 0, 0);
    }

    public void withName(String name) {
        this.name = name;
    }

    public void withID(long ID) {
        this.ID = ID;
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

    public void print() {
        System.out.println("ID: " + ID);
        System.out.println("Name: " + name);
        System.out.println("Threads amount: " + threadAmount);
        System.out.println("CPU usage: " + cpuUsage + "%");
        System.out.println("RAM usage: " + ramUsage);
        System.out.println("-----------------------");
    }
}

