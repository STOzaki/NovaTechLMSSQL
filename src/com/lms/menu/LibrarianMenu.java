package com.lms.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.RetrieveException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.model.Book;
import com.lms.model.Branch;
import com.lms.service.LibrarianServiceImpl;

public class LibrarianMenu {
	private LibrarianServiceImpl libraryService;
	
	private final Scanner inStream;
	private final Appendable outStream;
	
	private static final Logger LOGGER = Logger.getLogger(LibrarianMenu.class.getName());
	
	public LibrarianMenu(Scanner inStream, Appendable outStream) throws CriticalSQLException {
		this.inStream = inStream;
		this.outStream = outStream;

		try {
			libraryService = new LibrarianServiceImpl("production");
		} catch (CriticalSQLException e) {
			LOGGER.log(Level.WARNING, "Was not able to initialize library services", e);
			throw new CriticalSQLException("Was not able to initialize library services", e);
		}
	}
	
	public boolean start() throws CriticalSQLException {
		boolean libRun = true;
		while(libRun) {
			println("Type the number associated with the branch you manage.");
			List<Branch> listOfAllBranches = new ArrayList<>();
			try {
				listOfAllBranches = libraryService.getAllBranches();
			} catch (RetrieveException e1) {
				LOGGER.log(Level.WARNING, "Could not load list of all branches", e1);
				throw new CriticalSQLException("Could not load list of all branches", e1);
			}
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
	
	private boolean librarianOptions(Branch branch) throws CriticalSQLException {
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
	
	private boolean addCopiesOfBooks(Branch branch) throws CriticalSQLException {
		boolean addingCopies = true;
		while(addingCopies) {
			println("Type the number associated with the book you want to add copies of, to your branch.");
			List<Book> listOfAllBooks = new ArrayList<>();
			try {
				listOfAllBooks = libraryService.getAllBooks();
			} catch (RetrieveException e1) {
				LOGGER.log(Level.WARNING, "Could not get a list of all books", e1);
				throw new CriticalSQLException("Could not get a list of all books", e1);
			}
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
	
	private boolean addCopiesOfABook(Branch branch, Book book) throws CriticalSQLException {
		Map<Branch, Map<Book, Integer>> listOfAllCopies = new HashMap<>();
		try {
			listOfAllCopies = libraryService.getAllCopies();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get a list of book copies", e1);
			throw new CriticalSQLException("Failed to get a list of book copies", e1);
		}
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
			throw new CriticalSQLException("Failed to set new branch copy amount", e);
		}
		return true;
	}
	
	private boolean updateBranchDetails(Branch branch) throws CriticalSQLException {
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
			throw new CriticalSQLException("Failed to update branch", e);
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
