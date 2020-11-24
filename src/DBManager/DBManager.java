package DBManager;

import databaseInteract.*;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimeZone;

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
        String query = String.format(
                "INSERT INTO users(user_name, password) VALUES ('%s', '%s')",
                user.getName(), user.getPassword());
        int rows = statement.executeUpdate(query);
        System.out.printf("Added %d rows at table users\n", rows);
    }

    public void addProgram(ProgramTracker program, String user_name) throws SQLException {
        Statement statement = conn.createStatement();
        String query = String.format(
                "INSERT INTO program (program_name, user_id) " +
                        "VALUES ('%s', (SELECT user_id FROM users WHERE user_name='%s'))",
                program.getName(), user_name);
        int rows = statement.executeUpdate(query);
        System.out.printf("Added %d rows at table program\n", rows);
    }

    public void addHourInf(HourInf hourInf, String program_name) throws SQLException {
        ResourceUsage resource = hourInf.getResource();
        String sql = "INSERT INTO hourinfo (cpuUsage, ramUsage, program_id, thread_amount, timeActSum, timeSum, dataPackCount, creationDate) " +
                "VALUES (?, ?, (SELECT program_id FROM program WHERE program_name=?), ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setDouble(1, resource.getCpuUsage());
            preparedStatement.setLong(2, resource.getRamUsage());
            preparedStatement.setString(3, program_name);
            preparedStatement.setInt(4, resource.getThreadAmount());
            preparedStatement.setInt(5, hourInf.getTimeActSum());
            preparedStatement.setInt(6, hourInf.getTimeSum());
            preparedStatement.setInt(7, hourInf.getDataPackCount());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(hourInf.getCreationDate()));
            int rows = preparedStatement.executeUpdate();
            System.out.printf("Added %d rows at table resourceUsage\n", rows);
        }
    }

    public ArrayList<HourInf> getHourInfByProgramId(int id_p) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM resourceUsage WHERE program_id=" + id_p);
        ArrayList<HourInf> hourInfo = new ArrayList<>();

        while (resultSet.next()) {
            double cpu = resultSet.getDouble(2);
            long ram = resultSet.getInt(3);
            int thread = resultSet.getInt(5);
            int timeActSum = resultSet.getInt(6);
            int timeSum = resultSet.getInt(7);
            Timestamp creationDate = resultSet.getTimestamp(9);
            hourInfo.add(new HourInf(timeSum, timeActSum, thread, cpu, ram, LocalDateTime.ofInstant(Instant.ofEpochMilli(creationDate.getTime()), TimeZone.getDefault().toZoneId())));
        }
        return hourInfo;
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

    public User getUser(String user_name) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_name=" + user_name);
        if (!resultSet.next())
            throw new SQLDataException("Not exist user with name" + user_name);
        int id = resultSet.getInt(1);
        String password = resultSet.getString(3);

        return new User(id, user_name, password, getProgramsByUserId(id));
    }

    public User getUser(int id) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE user_id=" + id);
        if (!resultSet.next())
            throw new SQLDataException("Not exist user with id" + id);

        String name = resultSet.getString(2);
        String password = resultSet.getString(3);

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
