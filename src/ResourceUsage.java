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

    //эту хрень нижнюю нужно для всех файлов классов?
    public int get_threadAmount()
    {
        return threadAmount;
    }
    public void set_threadAmount(int threadAmount)
    {
        this.threadAmount = threadAmount;
    }
    public double get_cpuUsage()
    {
        return cpuUsage;
    }
    public void set_cpuUsage(double cpuUsage)
    {
        this.cpuUsage = cpuUsage;
    }
    public long get_ramUsage ()
    {
        return ramUsage;
    }
    public void set_ramUsage (long ramUsage)
    {
        this.ramUsage = ramUsage;
    }

}


