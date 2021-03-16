package databaseInteract;

import dataRecieve.DataPack;
import dataRecieve.ProgramClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DataPackToUserTest
{
    ArrayList<ProgramClass> programList = new ArrayList<>();
    ArrayList<DataPack> dataPackList = new ArrayList<>();
    Map<String, User> userList = new HashMap<>();
    ArrayList<ProgramTracker> programTrackerList = new ArrayList<>();
    ResourceUsage expectedResourceUsage1Hour1;
    ResourceUsage expectedResourceUsage1Hour2;
    HourInf expectedTracker1Hour1;

    @Before
    public void setUp()
    {
        ProgramClass testProgram1 = ProgramClass.aProgramClass();
        testProgram1.withName("GooseProgram");
        testProgram1.withCpuUsage(100);
        testProgram1.withID(66996699);
        testProgram1.withRamUsage(1);
        testProgram1.withThreadAmount(22);
        programList.add(testProgram1);

        ProgramClass testProgram2 = ProgramClass.aProgramClass();
        testProgram2.withName("SuperGoose");
        testProgram2.withCpuUsage(0);
        testProgram2.withID(3333333);
        testProgram2.withRamUsage(999999);
        testProgram2.withThreadAmount(3);
        programList.add(testProgram2);

        ProgramClass testProgram3 = ProgramClass.aProgramClass();
        testProgram3.withName("PooperGoose");
        testProgram3.withCpuUsage(50);
        testProgram3.withID(6666666);
        testProgram3.withRamUsage(34);
        testProgram3.withThreadAmount(9);
        programList.add(testProgram3);

        //ArrayList<ProgramClass>

        DataPack testDataPack1 = DataPack.aDataPack();
        testDataPack1.withName("Goose");
        testDataPack1.withActiveWindowProcessName("GooseProgram");
        testDataPack1.withCollectInterval(10000);
        testDataPack1.withPassword(new byte[16]);
        testDataPack1.withDateTime(LocalDateTime.of(2020, 12, 12, 21, 12, 12));
        ArrayList<ProgramClass> programClassesDataPack1 = new ArrayList<ProgramClass>();
        programClassesDataPack1.add(testProgram1);
        programClassesDataPack1.add(testProgram2);
        testDataPack1.withPrograms(programClassesDataPack1);
        dataPackList.add(testDataPack1);

        DataPack testDataPack2 = DataPack.aDataPack();
        testDataPack2.withName("Goose");
        testDataPack2.withActiveWindowProcessName("");
        testDataPack2.withCollectInterval(10000);
        testDataPack2.withPassword(new byte[16]);
        testDataPack2.withDateTime(LocalDateTime.of(2020, 12, 12, 21, 32, 12));
        ArrayList<ProgramClass> programClassesDataPack2 = new ArrayList<ProgramClass>();
        programClassesDataPack2.add(testProgram1);
        programClassesDataPack2.add(testProgram3);
        testDataPack2.withPrograms(programClassesDataPack2);
        dataPackList.add(testDataPack2);

        ResourceUsage expectedResourceUsage1Hour1 = ResourceUsage.aResourceUsage();
        expectedResourceUsage1Hour1.withThreadAmount(22);
        expectedResourceUsage1Hour1.withCpuUsage(100);
        expectedResourceUsage1Hour1.withRamUsage(1);
        this.expectedResourceUsage1Hour1 = expectedResourceUsage1Hour1;

        HourInf expectedTracker1Hour1 = HourInf.aHourInf();
        expectedTracker1Hour1.withDataPackCount(2);
        expectedTracker1Hour1.withCreationDate(LocalDateTime.of(2020, 12, 12, 21, 0, 0));
        expectedTracker1Hour1.withTimeSum(20000);
        expectedTracker1Hour1.withActTimeSum(10000);
        expectedTracker1Hour1.withResourceUsage(expectedResourceUsage1Hour1);
        this.expectedTracker1Hour1 = expectedTracker1Hour1;

        ProgramTracker expectedTracker1 = ProgramTracker.aProgramTracker();
        expectedTracker1.withID(66996699);
        expectedTracker1.withName("GooseProgram");
        expectedTracker1.withHourWork(new ArrayList<HourInf>(Arrays.asList(expectedTracker1Hour1)));

        ResourceUsage expectedResourceUsage1Hour2 = ResourceUsage.aResourceUsage();
        expectedResourceUsage1Hour2.withThreadAmount(3);
        expectedResourceUsage1Hour2.withCpuUsage(0);
        expectedResourceUsage1Hour2.withRamUsage(999999);
        this.expectedResourceUsage1Hour2 = expectedResourceUsage1Hour2;

        HourInf expectedTracker1Hour2 = HourInf.aHourInf();
        expectedTracker1Hour2.withDataPackCount(1);
        expectedTracker1Hour2.withCreationDate(LocalDateTime.of(2020, 12, 12, 21, 0, 0));
        expectedTracker1Hour2.withTimeSum(10000);
        expectedTracker1Hour2.withActTimeSum(0);
        expectedTracker1Hour2.withResourceUsage(expectedResourceUsage1Hour2);

        ProgramTracker expectedTracker2 = ProgramTracker.aProgramTracker();
        expectedTracker2.withID(3333333);
        expectedTracker2.withName("SuperGoose");
        expectedTracker2.withHourWork(new ArrayList<HourInf>(Arrays.asList(expectedTracker1Hour2)));

        ResourceUsage expectedResourceUsage1Hour3 = ResourceUsage.aResourceUsage();
        expectedResourceUsage1Hour3.withThreadAmount(9);
        expectedResourceUsage1Hour3.withCpuUsage(50);
        expectedResourceUsage1Hour3.withRamUsage(34);

        HourInf expectedTracker1Hour3 = HourInf.aHourInf();
        expectedTracker1Hour3.withDataPackCount(1);
        expectedTracker1Hour3.withCreationDate(LocalDateTime.of(2020, 12, 12, 21, 0, 0));
        expectedTracker1Hour3.withTimeSum(10000);
        expectedTracker1Hour3.withActTimeSum(0);
        expectedTracker1Hour3.withResourceUsage(expectedResourceUsage1Hour3);

        ProgramTracker expectedTracker3 = ProgramTracker.aProgramTracker();
        expectedTracker3.withID(6666666);
        expectedTracker3.withName("PooperGoose");
        expectedTracker3.withHourWork(new ArrayList<HourInf>(Arrays.asList(expectedTracker1Hour3)));

        User expectedUser1 = User.aUser();
        expectedUser1.withID(10);
        expectedUser1.withName("Goose");
        expectedUser1.withPassword(new byte[16]);
        expectedUser1.withPrograms(new ArrayList<ProgramTracker>(Arrays.asList(expectedTracker1, expectedTracker2, expectedTracker3)));
        userList.put(expectedUser1.getName(), expectedUser1);
    }

    @Test
    public void transformPacks()
    {
        DataPackToUser transformer = new DataPackToUser(new LinkedList<DataPack>(dataPackList), new HashMap<String, User>());
        transformer.TransformPacks();
        transformer.getUsers().get("Goose").finalizeObservations();

        Assert.assertTrue(transformer.getUsers().get("Goose").equals(userList.get("Goose")));
        //assert(transformer.getUsers().is(userList));
    }

    @Test
    public void resourceUsageConstructorTest1()
    {
        ResourceUsage resourceUsage = new ResourceUsage(22, 100, 1);
        Assert.assertTrue(resourceUsage.equals(expectedResourceUsage1Hour1));
    }

    @Test
    public void resourceUsageConstructorTest2()
    {
        ResourceUsage resourceUsage = new ResourceUsage(3, 0, 999999);
        Assert.assertTrue(resourceUsage.equals(expectedResourceUsage1Hour2));
    }

    @Test
    public void hourInfoConstructorTest2()
    {
        HourInf hourInf1 = new HourInf(20000, 10000, expectedResourceUsage1Hour1.getThreadAmount(), expectedResourceUsage1Hour1.getCpuUsage(), expectedResourceUsage1Hour1.getRamUsage(), LocalDateTime.of(2020, 12, 12, 21, 0, 0), 2);
        Assert.assertTrue(hourInf1.equals(expectedTracker1Hour1));
    }
}