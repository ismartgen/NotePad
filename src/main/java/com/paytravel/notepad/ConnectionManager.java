package com.paytravel.notepad;

import java.sql.*;

public class ConnectionManager {
    public static Connection getConnection() {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            conn.setAutoCommit(false);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

