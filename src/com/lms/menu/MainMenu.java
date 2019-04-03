package com.lms.menu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.io.IOException;
import java.io.Reader;

public class MainMenu {
	
	private final Scanner inStream;
	private final Appendable outStream;
	
	private AdminMenu adminMenu;
	private BorrowerMenu borrowerMenu;
	private LibrarianMenu librarianMenu;
	
	private static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());

	public MainMenu(Connection conn, Reader in, Appendable out) {
		this.inStream = new Scanner(in);
		this.outStream = out;
		
		// turn off auto commit
        try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "failed to set auto commit with error message: " + e.getMessage() +
					" for class " + e.getClass());
			println("Warning: we have a problem with setting up our connection to the database,");
			println("so you may experience so problems. (Thank you)");
		}
		
		adminMenu = new AdminMenu(conn, inStream, outStream);
		borrowerMenu = new BorrowerMenu(conn, inStream, outStream);
		librarianMenu = new LibrarianMenu(conn, inStream, outStream);
	}

	public void start() {
		boolean run = true;
		while(run) {
			println("What kind of user are you? ((A)dmin, (L)ibrarian, or (B)orrower) or (Q)uit");
			String userChoice = inStream.nextLine();
			switch(userChoice) {
			case "A": case "a": case "Admin":
				adminMenu.start();
				break;
			case "L": case "l": case "Librarian":
				librarianMenu.start();
				break;
			case "B": case "b": case "Borrower":
				borrowerMenu.start();
				break;
			case "Q": case "q": case "Quit":
				run = false;
				break;
			default:
				println("I am sorry but that is not a user type. Please try again.");
			}
		}
	}

	/**
	 * method print string and then moves to the next line
	 *
	 * @param String to be printed
	 * @author Jonathan Lovelace
	 */
	private void println(final String line) {
		try {
			outStream.append(line);
			outStream.append(System.lineSeparator());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "I/O Error occured while println a line");
		}
	}
}
