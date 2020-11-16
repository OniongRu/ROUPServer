package DBManager;

import databaseInteract.HourInf;
import databaseInteract.ProgramTracker;
import databaseInteract.ResourceUsage;
import databaseInteract.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class DBManager {

    private Connection conn;

    public DBManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            conn = getConnection();
            System.out.println("Connection to Store DB successful!");
        } catch (Exception ex) {
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }

    public void addUser(User user) throws SQLException {
        Statement statement = conn.createStatement();
        int rows = statement.executeUpdate(String.format(
                "INSERT users(user_id, user_name, login, password) VALUES (%d, '%s', '%s', '%s')"
                , user.getID(), user.getName(), "someLogin", user.getPassword()));
        System.out.printf("Added %d rows at table users\n", rows);
    }

    public void addProgram(ProgramTracker program, int id) throws SQLException {
        Statement statement = conn.createStatement();
        int rows = statement.executeUpdate(String.format(
                "INSERT program (program_name, user_id) VALUES ('%s', %d)"
                , program.getName(), id));
        System.out.printf("Added %d rows at table program\n", rows);
    }

    public void addResourceUsage(ResourceUsage resource, int id) throws SQLException {
        Statement statement = conn.createStatement();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int rows = statement.executeUpdate(String.format(
                "INSERT resourceUsage (date_using, cpuUsage, ramUsage, program_id, thread_amount) VALUES (%f, %d, %d, %d)"
                , resource.get_cpuUsage(), resource.get_ramUsage(), id, resource.get_threadAmount()));
        System.out.printf("Added %d rows at table resourceUsage\n", rows);
    }

    public ArrayList<HourInf> getHourInfByProgramId(int id_p) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM resourceUsage WHERE program_id=" + id_p);
        ArrayList<HourInf> hourInfs = new ArrayList<>();

        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            Date date =  new Date();//resultSet.getDate(2);
            double cpu = resultSet.getDouble(3);
            long ram = resultSet.getInt(4);
            int thread = resultSet.getInt(6);
            hourInfs.add(new HourInf(thread, cpu, ram, date));
        }
        return hourInfs;
    }

    public ArrayList<ProgramTracker> getProgramsByUserId(int id_u) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM program WHERE user_id=" + id_u);
        ArrayList<ProgramTracker> programs = new ArrayList<>();

        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String program_name = resultSet.getString(2);
            programs.add(new ProgramTracker(id, program_name, getHourInfByProgramId(id)));
        }
        return programs;
    }

    public User getUser(int id) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_id=" + id);
        resultSet.next();

        String name = resultSet.getString(2);
        String password = resultSet.getString(5);

        return new User(id, name, password, getProgramsByUserId(id));
    }

    private Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }
}
