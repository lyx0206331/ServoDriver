package com.adrian.servodriver.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static com.adrian.servodriver.utils.Constants.DB_PASSWORD;
import static com.adrian.servodriver.utils.Constants.DB_USER;

/**
 * Created by ranqing on 2018/1/18.
 */

public class FirebirdUtil {

    private static FirebirdUtil instance;

    private Connection con;

    public static FirebirdUtil getInstance() {
        if (instance == null) {
            instance = new FirebirdUtil();
        }
        return instance;
    }

    public void connect() {
        Properties ParamConnection = new Properties();
        ParamConnection.setProperty("user", DB_USER);
        ParamConnection.setProperty("password", DB_PASSWORD);
        String sCon = "jdbc:firebirdsql:embedded:/sdcard/Maxsine/firmware/DB_FIRMWARE.FDB";
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection(sCon, ParamConnection);

//            String sql = "";
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        FBDataSource
    }

    public void close() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        try {
            if (con != null && !con.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void readData() {

    }
}
