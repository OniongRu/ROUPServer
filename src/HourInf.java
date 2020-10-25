import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HourInf {
    //DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    Date date;
    private int timeSum;
    private int timeActSum;
    ResourceUsage resource;
    public HourInf(Date date, int timeSum, int timeActSum, int threadAmount, double cpuUsage, long ramUsage)
    {
        this.date = date;
        this.timeSum = timeSum;
        this.timeActSum = timeActSum;
        resource = new ResourceUsage(threadAmount, cpuUsage, ramUsage);
    }
}
