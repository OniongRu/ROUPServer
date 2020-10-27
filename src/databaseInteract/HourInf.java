package databaseInteract;

import databaseInteract.ResourceUsage;

import java.util.Date;

//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class HourInf {
    //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    Date date;
    private int timeSum;
    private int timeActSum;
    ResourceUsage resource;

    public Date getDate() {
        return date;
    }

    public int getTimeSum() {
        return timeSum;
    }

    public int getTimeActSum(){
        return timeActSum;
    }

    public ResourceUsage getResource() {
        return resource;
    }

    public HourInf(Date date, int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage)
    {
        this.date = date;
        this.timeSum = timeSum;
        this.timeActSum = timeActSum;
        resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
    }
}
