package org.example.database;

import org.example.models.BusModel;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sqLiteConnector {

    private static Connection connection;
    private static final String DATABASE_URL = "jdbc:sqlite:C:\\Users\\aatak\\Desktop\\sqlite\\ridewave.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            System.out.println("SQLite bağlantısı başarıyla oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("SQLite bağlantısı oluşturulurken hata oluştu: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("SQLite bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            System.err.println("SQLite bağlantısı kapatılırken hata oluştu: " + e.getMessage());
        }
    }


    public static void getAllUsers (Connection connection) {
        try {
            String sql = "SELECT * FROM Users";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String email = resultSet.getString("email");
                        System.out.println( "Name: " + name + "email : "+ email);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean authentication(String email, String password){
        try (Connection connection = connect()) {
            String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean createUserSqlite(String email,String password){
        String query = "INSERT INTO Users (email, password) VALUES (?, ?)";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            int count = preparedStatement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Kullanıcı oluşturulurken bir hata oluştu: " + e.getMessage(), e);
        }

    }


    public static List<String> getAllBusNames() {
        List<String> busNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            String query = "SELECT busName FROM AllBuses";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    busNames.add(resultSet.getString("busName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return busNames;
    }

    public static BusModel retrieveBusDataFromDatabase(String busName) {
        BusModel busModel = null;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AllBuses WHERE busName LIKE ?")) {

            preparedStatement.setString(1, "%" + busName + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Process the result set and get the first bus data
                if (resultSet.next()) {
                    busModel = new BusModel();
                    busModel.setBusName(resultSet.getString("busName"));
                    busModel.setFirstStation(resultSet.getString("firstStation"));
                    busModel.setLastStation(resultSet.getString("lastStation"));
                    busModel.setCurrentStation(resultSet.getString("currentStation"));
                    busModel.setBusCrowd(resultSet.getInt("busCrowd"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }

        return busModel;
    }
}
