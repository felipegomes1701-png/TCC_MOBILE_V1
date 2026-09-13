package com.example.barbeariadooracle;

import java.sql.Connection;
import java.sql.DriverManager;

public class Databaseconnection {

    private static final String IP = "192.168.15.5";
    private static final String PORT = "1433";
    private static final String DATABASE = "db_barbearia";
    private static final String USER = "usr_barbearia";
    private static final String PASSWORD = "Senha@123456";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            // Adicionado ssl=request e socketTimeout para evitar travamentos
            String connectionUrl = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE + ";ssl=request;socketTimeout=10";

            DriverManager.setLoginTimeout(10);
            conn = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}