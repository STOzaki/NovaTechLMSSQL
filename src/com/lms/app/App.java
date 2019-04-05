package com.lms.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.menu.MainMenu;

public class App {

	public static void main(String[] args) {
		final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
		
		try(InputStreamReader in = new InputStreamReader(System.in)) {
			final MainMenu menu = new MainMenu(in, System.out);
			menu.start();
		} catch (final IOException except) {
			LOGGER.log(Level.SEVERE, "I/O error dealing with System input", except);
		} catch (CriticalSQLException e) {
			System.out.println("There seems to have been an issue with starting this application");
		}
	}
}