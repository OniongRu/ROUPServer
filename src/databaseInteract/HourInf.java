package databaseInteract;


import dataRecieve.ProgramClass;
import java.util.Date;


//Only getters here because there is no point in changing pack info https://vk.com/sticker/1-163-64
public class HourInf {
    //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    private Date creationDate;
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

    public ResourceUsage getResource() {
        return resource;
    }

    public Date getCreationDate() { return creationDate; }

    public HourInf(int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage, Date creationDate)
    {

        this.timeSum = timeSum;
        this.creationDate =creationDate;
        this.timeActSum = timeActSum;
        this.resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
        this.dataPackCount=0;
    }

    public HourInf(int threadAmount, double cpuUsage, long ramUsage, Date creationDate)
    {

        this.timeSum = 0;
        this.creationDate =creationDate;
        this.timeActSum = 0;
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
    
    public HourInf(Date date)
    {
        this.creationDate=date;
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
