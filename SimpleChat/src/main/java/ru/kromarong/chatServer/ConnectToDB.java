package ru.kromarong.chatServer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ConnectToDB {
    private static Map<String, String> clientList = new HashMap<>();;

    public static ResultSet getAllUsers() {
        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientDB.db")) {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM client");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static boolean findUser(String username, String password) throws SQLException {
        boolean result = false;

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientDB.db")) {
            PreparedStatement findUser = connection.prepareStatement("SELECT * FROM client WHERE username = ? AND password = ?");
            findUser.setString(1, username);
            findUser.setString(2, password);
            findUser.executeQuery();

            if (findUser != null) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return result;
        }

    public static boolean findUser(String username){
        boolean result = false;

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientDB.db")) {
            PreparedStatement findUser = connection.prepareStatement("SELECT * FROM client WHERE username = ?");
            findUser.setString(1, username);
            findUser.executeQuery();

            if (findUser != null) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void updateUsername(String username, String newName) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientDB.db")) {
            PreparedStatement updateUser = connection.prepareStatement("UPDATE client SET username = ? WHERE username = ?");
            updateUser.setString(1, newName);
            updateUser.setString(2, username);
            updateUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createClient(String log, String user, String pwd){
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientDB.db")) {
            PreparedStatement createUser = connection.prepareStatement("INSERT INTO client (login, username, password)" +
                    "VALUES(?,?,?)");
            createUser.setString(1, log);
            createUser.setString(2, user);
            createUser.setString(3, pwd);
            createUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
