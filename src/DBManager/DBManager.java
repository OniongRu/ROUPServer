package DBManager;

import databaseInteract.Program;
import databaseInteract.ResourceUsage;
import databaseInteract.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
                , user.getID(), user.getName(), user.getLogin(), user.getPassword()));
        System.out.printf("Added %d rows at table users\n", rows);
    }

    public void addProgram(Program program, int id) throws SQLException {
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
                "INSERT resourceUsage (date_using, cpuUsage, ramUsage, program_id, threadAmount) VALUES (%f, %d, %d, %d)"
                , resource.get_cpuUsage(), resource.get_ramUsage(), id, resource.get_threadAmount()));
        System.out.printf("Added %d rows at table resourceUsage\n", rows);
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
