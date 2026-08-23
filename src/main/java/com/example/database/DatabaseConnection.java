package com.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // trivia_db байсныг trivia_battle болгож засна
    private static final String URL = "jdbc:mysql://localhost:3306/trivia_battle";
    private static final String USER = "root";
    private static final String PASSWORD = "1504"; // MySQL нууц үгээ энд бичээрэй

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
