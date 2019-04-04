package com.lms.service.util;

import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.CriticalSQLException;

import java.util.Properties;

public final class ConnectingToDataBase {
	final static Logger LOGGER = Logger.getLogger(ConnectingToDataBase.class.getName());

	public static Connection connectingToDataBase(String env) throws CriticalSQLException {
		Connection conn = null;
		String user = "";
		String password = "";
		String url = "";

		try (FileInputStream file = new FileInputStream(".properties")) {
			
			Properties prop = new Properties();
			prop.load(file);
			
			user = prop.getProperty("user");
			password = prop.getProperty("password");

			if(env.equals("test")) {
				url = prop.getProperty("testUrl");
			} else if(env.equals("production")) {
				url = prop.getProperty("url");
			} else {
				url = prop.getProperty("url");
			}
			

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O Error cannot read .property file: " + e.getMessage());
			throw new CriticalSQLException("I/O Error cannot read .property file", e);
		}
		
		try {
			conn = (Connection) DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException ex) {
		    // handle any errors
			LOGGER.log(Level.WARNING, "SQLException: " + ex.getMessage() + " WITH SQLState: " +
					ex.getSQLState() + " WITH VendorError: " + ex.getErrorCode());
			throw new CriticalSQLException("Unable to connection to the database", ex);
		}
	}
	
	public static void closingConnection(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "WARNING: Unable to close connection to database");
			}
		}
	}
}
