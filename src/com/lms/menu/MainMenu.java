package com.lms.menu;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.CriticalSQLException;

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

	public MainMenu(Reader in, Appendable out) throws CriticalSQLException {
		this.inStream = new Scanner(in);
		this.outStream = out;
		
		adminMenu = new AdminMenu(inStream, outStream);
		borrowerMenu = new BorrowerMenu(inStream, outStream);
		librarianMenu = new LibrarianMenu(inStream, outStream);
	}

	public void start() {
		try {
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
		} catch (CriticalSQLException e) {
			println("There seems to have been an internal error. Unfortunaly we will have to disconnect.");
			println("Have a nice day!");
			LOGGER.log(Level.SEVERE, "Critical Error with internal server!");
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
