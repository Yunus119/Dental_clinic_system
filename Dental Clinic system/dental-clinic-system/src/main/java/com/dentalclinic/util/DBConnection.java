package com.dentalclinic.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    // returns a new connection to the database every time it's called
    public static Connection getConnection() throws SQLException {

        Properties props = new Properties();

        try {
            // load db.properties from the resources folder
            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);
        } catch (IOException e) {
            throw new SQLException("Could not load db.properties", e);
        }

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        // this actually connects to MySQL using the values above
        return DriverManager.getConnection(url, username, password);
    }
}