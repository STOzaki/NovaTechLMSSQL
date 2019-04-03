package com.lms.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.menu.MainMenu;

public class App {

	public static void main(String[] args) {
		final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
		Connection conn = null;
		List<String> authentication = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(".config"));
			String nextLine = "";
			while((nextLine = br.readLine()) != null) {
				authentication.add(nextLine);
			}
			br.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O Error cannot read .config file");
		}

		try {
			conn = (Connection) DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC",
				authentication.get(0), authentication.get(1));
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		
		try(InputStreamReader in = new InputStreamReader(System.in)) {
			final MainMenu menu = new MainMenu(conn, in, System.out);
			menu.start();
		} catch (final IOException except) {
			LOGGER.log(Level.SEVERE, "I/O error dealing with System input", except);
		}
	}
}
