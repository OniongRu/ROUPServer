package DBManager;

import GUI.Controller;
import databaseInteract.HourInf;
import databaseInteract.ProgramTracker;
import databaseInteract.ResourceUsage;
import databaseInteract.User;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class DBManager
{

    private Connection conn;

    public DBManager() throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        //try
        //{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            conn = getConnection();
        /*} catch (Exception ex)
        {
            Controller.getInstance().showErrorMessage("Connection to database failed!");
            Controller.getInstance().onTurnedOff();
            //System.out.println("Connection failed...");
            //System.out.println(ex);
        }*/
    }

    public void addUser(User user) throws SQLException
    {
        String query = "INSERT INTO users(user_name, password, privilege) VALUES (?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, user.getName());
        statement.setBytes(2, user.getPassword());
        statement.setInt(3, 0);

        int rows = statement.executeUpdate();
        System.out.printf("Added %d rows at table users\n", rows);
    }

    public boolean isProgramExists(String userName, String programName) throws SQLException
    {
        String sqlSmoll = "SELECT program_id FROM program WHERE program_name=\"" + programName +
                "\" AND user_id=(SELECT user_id FROM users WHERE user_name=\"" + userName + "\")";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlSmoll))
        {
            ResultSet resultSet = preparedStatement.executeQuery(sqlSmoll);
            if (!resultSet.next())
            {
                return false;
            }
            if (resultSet.next())
            {
                throw new SQLException();
            }
            return true;
        }
    }

    public void addProgram(ProgramTracker program, String user_name) throws SQLException
    {
        boolean isProgramInDB = true;
        try
        {
            isProgramInDB = isProgramExists(user_name, program.getName());
        } catch (SQLException e)
        {
            Controller.getInstance().showStatusMessage("Fatal: malformed database\nor unqualified developer", Paint.valueOf("#910415"));
            return;
        }
        if (!isProgramInDB)
        {
            Statement statement = conn.createStatement();
            String query = String.format(
                    "INSERT INTO program (program_name, user_id) " +
                            "VALUES ('%s', (SELECT user_id FROM users WHERE user_name='%s'))",
                    program.getName(), user_name);
            int rows = statement.executeUpdate(query);
            System.out.printf("Added %d rows at table program\n", rows);
        }
    }

    public void addHourInf(HourInf hourInf, String programName, String userName) throws SQLException
    {
        HourInf hourInfFromDB = getHourInf(programName, userName, hourInf.getCreationDate());
        hourInf.mergeFinalizedHourInfo(hourInfFromDB);

        String deleteQuery = "DELETE FROM hourinfo WHERE creationDate=\"" + Timestamp.valueOf(hourInf.getCreationDate())
                + "\" AND program_id = (SELECT program_id FROM program WHERE program_name=\"" + programName +
                "\" AND user_id=(SELECT user_id FROM users WHERE user_name=\"" + userName + "\"))";

        try
        {
            PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
            deleteStatement.executeUpdate();
        } catch (SQLException e)
        {
            Controller.getInstance().showStatusMessage("Replacing HourInfo failed\nCould not delete old information");
            return;
        }

        String sql = "INSERT INTO hourinfo (cpuUsage, ramUsage, program_id, thread_amount, timeActSum, timeSum, dataPackCount, creationDate) " +
                "VALUES (?, ?, (SELECT program_id FROM program WHERE program_name=? " +
                "AND user_id=(SELECT user_id FROM users WHERE user_name=?)), ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            ResourceUsage resource = hourInf.getResource();
            preparedStatement.setDouble(1, resource.getCpuUsage());
            preparedStatement.setLong(2, resource.getRamUsage());
            preparedStatement.setString(3, programName);
            preparedStatement.setString(4, userName);
            preparedStatement.setInt(5, resource.getThreadAmount());
            preparedStatement.setInt(6, hourInf.getTimeActSum());
            preparedStatement.setInt(7, hourInf.getTimeSum());
            preparedStatement.setInt(8, hourInf.getDataPackCount());
            preparedStatement.setTimestamp(9, Timestamp.valueOf(hourInf.getCreationDate()));
            int rows = preparedStatement.executeUpdate();
            System.out.printf("Added %d rows at table resourceUsage\n", rows);
        } catch (SQLException e)
        {
            Controller.getInstance().showStatusMessage("Couldn't add hourly information");
            System.out.println("GOOSE!");
        }
    }

    public HourInf getHourInf(String programName, String userName, LocalDateTime Date) throws SQLException
    {
        HourInf hourInfo;
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM hourinfo WHERE creationDate=\"" + Timestamp.valueOf(Date) +
                "\" AND program_id = (SELECT program_id FROM program WHERE program_name=\"" + programName +
                "\" AND user_id=(SELECT user_id FROM users WHERE user_name=\"" + userName + "\"))";
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next())
        {
            double cpu = resultSet.getDouble(2);
            long ram = resultSet.getInt(3);
            int thread = resultSet.getInt(5);
            int timeActSum = resultSet.getInt(6);
            int timeSum = resultSet.getInt(7);
            int dataPackCount = resultSet.getInt(8);
            Timestamp creationDate = resultSet.getTimestamp(9);
            hourInfo = new HourInf(timeSum, timeActSum, thread, cpu, ram,
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(creationDate.getTime()), TimeZone.getDefault().toZoneId()),
                    dataPackCount);
        } else
        {
            hourInfo = new HourInf();
        }

        return hourInfo;
    }

    public ArrayList<HourInf> getHourInfByProgramId(int id_p) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM hourinfo WHERE program_id=" + id_p);
        ArrayList<HourInf> hourInfo = new ArrayList<>();

        while (resultSet.next())
        {
            double cpu = resultSet.getDouble(2);
            long ram = resultSet.getInt(3);
            int thread = resultSet.getInt(5);
            int timeActSum = resultSet.getInt(6);
            int timeSum = resultSet.getInt(7);
            Timestamp creationDate = resultSet.getTimestamp(9);
            hourInfo.add(new HourInf(timeSum, timeActSum, thread, cpu, ram,
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(creationDate.getTime()), TimeZone.getDefault().toZoneId())));
        }
        return hourInfo;
    }

    public ArrayList<ProgramTracker> getProgramsByUserId(int id_u) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM program WHERE user_id=" + id_u);
        ArrayList<ProgramTracker> programs = new ArrayList<>();

        while (resultSet.next())
        {
            int id = resultSet.getInt(1);
            String program_name = resultSet.getString(2);
            programs.add(new ProgramTracker(id, program_name, getHourInfByProgramId(id)));
        }
        return programs;
    }

    public User getUser(String user_name) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_name=\"" + user_name + "\"");
        if (!resultSet.next())
            throw new SQLDataException("Not exist user with name" + user_name);
        int id = resultSet.getInt(1);
        byte[] password = resultSet.getBytes(3);

        return new User(id, user_name, password, getProgramsByUserId(id));
    }

    public boolean isUserExists(String userName) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT password FROM users WHERE user_name=\"" + userName + "\"");
        if (!resultSet.next())
        {
            return false;
        }
        return true;
    }

    public boolean isUserValid(String userName, byte[] password) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT password FROM users WHERE user_name=\"" + userName + "\"");
        if (!resultSet.next() || !Arrays.equals(resultSet.getBytes(1), password))
        {
            return false;
        }
        if (resultSet.next())
        {
            return false;
        }
        return true;
    }

    public boolean isUserValid(User user) throws SQLException
    {
        return isUserValid(user.getName(), user.getPassword());
    }

    public User getUser(int id) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_id=" + id);
        if (!resultSet.next())
            throw new SQLDataException("Not exist user with id" + id);

        String name = resultSet.getString(2);
        byte[] password = resultSet.getBytes(3);

        return new User(id, name, password, getProgramsByUserId(id));
    }

    public Object executeRequest(String request) throws SQLException
    {
        Statement statement = conn.createStatement();
        Object resultSet = statement.executeQuery("SELECT" + request);
        return resultSet;
    }

    public Set<String> getAllUserNames() throws SQLException
    {
        Set<String> userNames = new HashSet<>();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT user_name FROM users");
        while (resultSet.next())
        {
            userNames.add(resultSet.getString(1));
        }
        return userNames;
    }

    public Set<String> getAllProgramNames() throws SQLException
    {
        Set<String> programNames = new HashSet<>();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT program_name FROM program");
        while (resultSet.next())
        {
            programNames.add(resultSet.getString(1));
        }
        return programNames;
    }

    public ArrayList<HourInf> getHourInfsByProgramIDAndTimeInterval(int programID, LocalDateTime from, LocalDateTime to) throws SQLException
    {
        ArrayList<HourInf> hourInfoList = new ArrayList<>();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM hourinfo WHERE program_id = " + programID + " AND creationDate > \"" + Timestamp.valueOf(from) + "\" AND creationDate < \"" + Timestamp.valueOf(to) + "\"");
        while (resultSet.next())
        {
            hourInfoList.add(new HourInf(
                    resultSet.getInt(7),
                    resultSet.getInt(6),
                    resultSet.getInt(5),
                    resultSet.getDouble(2),
                    resultSet.getLong(3),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(resultSet.getTimestamp(9).getTime()), TimeZone.getDefault().toZoneId()),
                    resultSet.getInt(8)
            ));
        }
        return hourInfoList;
    }

    public ProgramTracker getProgramByNameAndUser(String programName, int userId, LocalDateTime from, LocalDateTime to) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM program WHERE program_name = \"" + programName + "\" AND user_id = " + userId);
        ProgramTracker programTracker = null;
        if (resultSet.next())
        {
            programTracker = new ProgramTracker(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    getHourInfsByProgramIDAndTimeInterval(resultSet.getInt(1), from, to)
            );
        }
        if (resultSet.next())
        {
            throw new SQLException();
        }
        return programTracker;
    }

    public User getUserWithPrograms(String userName, ArrayList<String> programNames, LocalDateTime from, LocalDateTime to) throws SQLException
    {
        ArrayList<ProgramTracker> programs = new ArrayList<>();

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_name = \"" + userName + "\"");
        User user = null;
        if (resultSet.next())
        {
            ArrayList<ProgramTracker> programTrackers = new ArrayList<>();
            for (String programName : programNames)
            {
                programTrackers.add(getProgramByNameAndUser(programName, resultSet.getInt(1), from, to));
            }
            user = new User(resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getBytes(3),
                    programTrackers
            );
        } else
        {
            throw new SQLException();
        }
        if (resultSet.next())
        {
            throw new SQLException();
        }
        return user;
    }

    public int getPrivilege(String userName) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT privilege FROM users WHERE user_name = \"" + userName + "\"");
        int privilege = 0;
        if (resultSet.next())
        {
            privilege = resultSet.getInt(1);
        } else
        {
            return -1;
        }
        if (resultSet.next())
        {
            throw new SQLException();
        }
        return privilege;
    }

    public int getPrivilege(int ID) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT privilege FROM users WHERE user_id = " + ID);
        int privilege;
        if (resultSet.next())
        {
            privilege = resultSet.getInt(1);
        } else
        {
            throw new SQLException();
        }
        if (resultSet.next())
        {
            throw new SQLException();
        }
        return privilege;
    }

    private Connection getConnection() throws SQLException, IOException
    {
        String url = GUI.Properties.getInstance().getDatabaseConnection();
        String login = Controller.getInstance().getLogin();
        String password = Controller.getInstance().getPassword();

        return DriverManager.getConnection(url, login, password);
    }
}
