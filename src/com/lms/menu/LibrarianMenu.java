package com.lms.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Book;
import com.lms.model.Branch;
import com.lms.service.LibrarianServiceImpl;

public class LibrarianMenu {
	private LibrarianServiceImpl libraryService;
	
	private final Scanner inStream;
	private final Appendable outStream;
	
	private static final Logger LOGGER = Logger.getLogger(LibrarianMenu.class.getName());
	
	public LibrarianMenu(Connection conn, Scanner inStream, Appendable outStream) {
		this.inStream = inStream;
		this.outStream = outStream;
		
		// turn off auto commit
        try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "failed to set auto commit with error message: " + e.getMessage() +
					" for class " + e.getClass());
			println("Warning: we have a problem with setting up our connection to the database,");
			println("so you may experience so problems. (Thank you)");
		}

		libraryService = new LibrarianServiceImpl(new LibraryBranchDaoImpl(conn), new BookDaoImpl(conn),
				new CopiesDaoImpl(conn), conn);
	}
	
	public boolean start() {
		boolean libRun = true;
		while(libRun) {
			println("Type the number associated with the branch you manage.");
			List<Branch> listOfAllBranches = libraryService.getAllBranches();
			printList(listOfAllBranches);
			try {
				int branchNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				branchNum--;
				if(branchNum == listOfAllBranches.size()) {
					println("Back to Main Menu");
					libRun = false;
				} else if(branchNum < listOfAllBranches.size() && branchNum >= 0) {
					librarianOptions(listOfAllBranches.get(branchNum));
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean librarianOptions(Branch branch) {
		boolean libOptions = true;
		while(libOptions) {
			println("Choose an option:");
			println("1)Update the details of the Library");
			println("2)Add copies of Book to the Branch");
			println("3)Quit to previous");
			try {
				int libOperationOption = Integer.parseInt(inStream.nextLine());
				switch(libOperationOption) {
				case 1:
					updateBranchDetails(branch);
					break;
				case 2:
					addCopiesOfBooks(branch);
					break;
				case 3:
					libOptions = false;
					break;
				default:
					println("That is not an option, please try again");
					break;
				}
				
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean addCopiesOfBooks(Branch branch) {
		boolean addingCopies = true;
		while(addingCopies) {
			println("Type the number associated with the book you want to add copies of, to your branch.");
			List<Book> listOfAllBooks = libraryService.getAllBooks();
			printList(listOfAllBooks);
			try {
				int bookNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				bookNum--;
				if(bookNum == listOfAllBooks.size()) {
					println("Back to Librarian Options");
					addingCopies = false;
				} else if(bookNum < listOfAllBooks.size() && bookNum >= 0) {
					addCopiesOfABook(branch, listOfAllBooks.get(bookNum));
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean addCopiesOfABook(Branch branch, Book book) {
		Map<Branch, Map<Book, Integer>> listOfAllCopies = libraryService.getAllCopies();
		int existingNumOfCopies = 0;
		if(listOfAllCopies.containsKey(branch)) {
			Map<Book, Integer> listOfAllCopiesOfABranch = listOfAllCopies.get(branch);

			if(listOfAllCopiesOfABranch.containsKey(book)) {
				existingNumOfCopies = listOfAllCopiesOfABranch.get(book);
			}
		}
		println("Existing number of copies: " + String.valueOf(existingNumOfCopies));
		println("Enter new number of copies:");
		try {
			int newNoOfCopies = Integer.parseInt(inStream.nextLine());
			libraryService.setBranchCopies(branch, book, newNoOfCopies);
			println("Update Successful");
		} catch (NumberFormatException e) {
			println("That is not a number");
		} catch (UnknownSQLException e) {
			LOGGER.log(Level.WARNING, "Failed to set branch copies: " + e.getMessage());
			println("I am sorry but there seems to have been a problem with updating the number of copies in " +
					branch.getName() + " for " + book.getTitle());
		}
		return true;
	}
	
	private boolean updateBranchDetails(Branch branch) {
		println("You have chosen to update the Branch with Branch Id: " + branch.getId() +
	" and Branch Name: " + branch.getName() + ", located at " + branch.getAddress() + ".");
		println("Enter 'quit' at any prompt to cancel operation.");
		
		println("Please enter new branch name or enter N/A for no change:");
		String newName = inStream.nextLine();
		if(newName.equals("quit")) {
			return true;
		} else if(!newName.equals("N/A")){
			branch.setName(newName);
		}
		
		println("Please enter new branch address or enter N/A for no change:");
		String newAddress = inStream.nextLine();
		if(newAddress.equals("quit")) {
			return true;
		} else if(!newAddress.equals("N/A")){
			branch.setName(newAddress);
		}
		
		try {
			libraryService.updateBranch(branch);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed update branch: " + e);
			println("Unfortunaly, we were unable to update the branch details for " + branch.getName());
		}
		println("Successfully Updated");
		return true;
	}
	
	private <T> void printList(List<T> list) {
		list.stream().forEach(l -> {
			try {
				outStream.append(String.valueOf(list.indexOf(l) + 1) + ") ");
				println(l.toString());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "I/O Error while iterating through a list");
			}
		});
		println(String.valueOf(list.size() + 1 + ") " + "Quit"));
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
