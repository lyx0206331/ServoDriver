package com.adrian.servodriver.utils;

import org.firebirdsql.jdbc.FBDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by ranqing on 2018/1/18.
 */

public class FirebirdUtil {

    private static FirebirdUtil instance;

    public static FirebirdUtil getInstance() {
        if (instance == null) {
            instance = new FirebirdUtil();
        }
        return instance;
    }

    public void connect() {
        Properties ParamConnection = new Properties();
        ParamConnection.setProperty("user", "admin");
        ParamConnection.setProperty("password", "123");
        String sCon = "jdbc:firebirdsql:embedded:/sdcard/Maxsine/firmware/DB_FIRMWARE.FDB";
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            Connection con = DriverManager.getConnection(sCon, ParamConnection);

            String sql = "";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        FBDataSource
    }

    public void readData() {

    }
}
