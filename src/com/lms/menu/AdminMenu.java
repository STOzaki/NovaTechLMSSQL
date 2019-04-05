package com.lms.menu;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.RetrieveException;
import com.lms.customExceptions.UpdateException;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.model.Publisher;
import com.lms.service.AdministratorServiceImpl;

public class AdminMenu {
	private AdministratorServiceImpl adminService;
	
	private final Scanner inStream;
	private final Appendable outStream;
	
	private static final Logger LOGGER = Logger.getLogger(AdminMenu.class.getName());

	public AdminMenu(Scanner inStream, Appendable outStream) throws CriticalSQLException {
		this.inStream = inStream;
		this.outStream = outStream;
	}
	
	public boolean start() throws CriticalSQLException {
		try {
			adminService = new AdministratorServiceImpl("production");
			boolean accessRun = true;
			while(accessRun) {
				println("What would you like to do?");
				println("1) Add");
				println("2) Update");
				println("3) Delete");
				println("4) Over-ride Due Date for a Book Loan");
				println("5) Return to Main Menu");
				try {
					int adminManipulatingChoice = Integer.parseInt(inStream.nextLine());
					switch(adminManipulatingChoice) {
					case 1:
						addingToADataBase();
						break;
					case 2:
						updateToADataBase();
						break;
					case 3:
						deleteToADataBase();
						break;
					case 4:
						overrideDueDate();
						break;
					case 5:
						accessRun = false;
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
		} catch (CriticalSQLException e) {
			throw new CriticalSQLException("Internal service error with the administrator service", e);
		} finally {
			try {
				adminService.closeConnection();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to close connection");
			}
		}
	}

	private boolean overrideDueDate() throws CriticalSQLException {
		List<Loan> listOfLoans = new ArrayList<>();
		try {
			listOfLoans = adminService.getAllLoans();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all loans", e1);
			throw new CriticalSQLException("Failed to get all loans", e1);
		}
		
		println("Which loan would you like to override the due date");
		printList(listOfLoans);
		Loan loanToOverride = pickingFromAList(listOfLoans);
		if(loanToOverride == null) {
			return false;
		}
		
		LocalDate newDueDate = null;
		boolean runUntilNewDue = true;
		while(runUntilNewDue) {
			try {
				println("What would you like the new due date to be? (Please format like yyyy-mm-dd)");
				newDueDate = LocalDate.parse(inStream.nextLine());
				runUntilNewDue = false;
			} catch (DateTimeException dte) {
				println("I am sorry but that is no the right data format (Needs to be like yyyy-mm-dd");
			}
		}
		try {
			adminService.overrideDueDateForLoan(loanToOverride.getBook(), loanToOverride.getBorrower(),
					loanToOverride.getBranch(), newDueDate);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to override due date");
			println("We were unable to override due date");
			throw new CriticalSQLException("Failed to override due date", e);
		} catch (RetrieveException e) {
			LOGGER.log(Level.WARNING, "Failed to override due date", e);
			throw new CriticalSQLException("Failed to override due date", e);
		}
		return true;
	}
	
	private boolean deleteToADataBase() throws CriticalSQLException {
		boolean runDeleteToDataBase = true;
		while(runDeleteToDataBase) {
			println("Which object in a database would you like to delete?");
			listOptionsForDataBases();
			try {
				int borrowerChoice = Integer.parseInt(inStream.nextLine());
				switch(borrowerChoice) {
				case 1:
					deleteToBook();
					break;
				case 2:
					deleteToAuthor();
					break;
				case 3:
					deleteToPublisher();
					break;
				case 4:
					deleteToBranch();
					break;
				case 5:
					deleteToBorrower();
					break;
				case 6:
					runDeleteToDataBase = false;
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
	
	private boolean deleteToBook() throws CriticalSQLException {
		List<Book> bookList = new ArrayList<>();
		try {
			bookList = adminService.getAllBooks();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all books", e1);
			throw new CriticalSQLException("Failed to get all books", e1);
		}
		println("Which book would you like to delete?");
		printList(bookList);
		Book deletingBook = pickingFromAList(bookList);
		if(deletingBook == null) {
			return false;
		}
		try {
			adminService.deleteBook(deletingBook);
		} catch (DeleteException e) {
			LOGGER.log(Level.WARNING, "Failed to delete book");
			println("We could not delete your requested book");
			throw new CriticalSQLException("Failed to delete book", e);
		}
		return true;
	}

	private boolean deleteToAuthor() throws CriticalSQLException {
		List<Author> authorList = new ArrayList<>();
		try {
			authorList = adminService.getAllAuthors();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all authors", e1);
			throw new CriticalSQLException("Failed to get all authors", e1);
		}
		println("Which author would you like to delete?");
		printList(authorList);
		Author deletingAuthor = pickingFromAList(authorList);
		if(deletingAuthor == null) {
			return false;
		}
		try {
			adminService.deleteAuthor(deletingAuthor);
		} catch (DeleteException e) {
			LOGGER.log(Level.WARNING, "Failed to delete author");
			println("We could not delete your requested author");
			throw new CriticalSQLException("Failed to delete author", e);
		}
		return true;
	}

	private boolean deleteToPublisher() throws CriticalSQLException {
		List<Publisher> publisherList = new ArrayList<>();
		try {
			publisherList = adminService.getAllPublishers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all publishers", e1);
			throw new CriticalSQLException("Failed to get all publishers", e1);
		}
		println("Which publisher would you like to delete?");
		printList(publisherList);
		Publisher deletingPublisher = pickingFromAList(publisherList);
		if(deletingPublisher == null) {
			return false;
		}
		try {
			adminService.deletePublisher(deletingPublisher);
		} catch (DeleteException e) {
			LOGGER.log(Level.WARNING, "Failed to delete publisher");
			println("We could not delete your requested publisher");
			throw new CriticalSQLException("Failed to delete publisher", e);
		}
		return true;
	}

	private boolean deleteToBranch() throws CriticalSQLException {
		List<Branch> branchList = new ArrayList<>();
		try {
			branchList = adminService.getAllBranches();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all branches", e1);
			throw new CriticalSQLException("Failed to get all branches", e1);
		}
		println("Which branch would you like to delete?");
		printList(branchList);
		Branch deletingBranch = pickingFromAList(branchList);
		if(deletingBranch == null) {
			return false;
		}
		try {
			adminService.deleteBranch(deletingBranch);
		} catch (DeleteException e) {
			LOGGER.log(Level.WARNING, "Failed to delete branch");
			println("We could not delete your requested library branch");
			throw new CriticalSQLException("Failed to delete branch", e);
		}
		return true;
	}

	private boolean deleteToBorrower() throws CriticalSQLException {
		List<Borrower> borrowerList = new ArrayList<>();
		try {
			borrowerList = adminService.getAllBorrowers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all borrowers", e1);
			throw new CriticalSQLException("Failed to get all borrowers", e1);
		}
		println("Which borrower would you like to delete?");
		printList(borrowerList);
		Borrower deletingBorrower = pickingFromAList(borrowerList);
		if(deletingBorrower == null) {
			return false;
		}
		try {
			adminService.deleteBorrower(deletingBorrower);
		} catch (DeleteException e) {
			LOGGER.log(Level.WARNING, "Failed to delete borrower");
			println("We could not delete your requested borrower");
			throw new CriticalSQLException("Failed to delete borrower", e);
		}
		return true;
	}
	
	private boolean updateToADataBase() throws CriticalSQLException {
		boolean runAddingToDataBase = true;
		while(runAddingToDataBase) {
			println("Which database would you like to update?");
			listOptionsForDataBases();
			try {
				int borrowerChoice = Integer.parseInt(inStream.nextLine());
				switch(borrowerChoice) {
				case 1:
					updateToBook();
					break;
				case 2:
					updateToAuthor();
					break;
				case 3:
					updateToPublisher();
					break;
				case 4:
					updateToBranch();
					break;
				case 5:
					updateToBorrower();
					break;
				case 6:
					runAddingToDataBase = false;
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
	
	private boolean updateToBook() throws CriticalSQLException {
		List<Book> bookList = new ArrayList<>();
		List<Author> authorList = new ArrayList<>();
		List<Publisher> publisherList = new ArrayList<>();
		try {
			authorList = adminService.getAllAuthors();
			bookList = adminService.getAllBooks();
			publisherList = adminService.getAllPublishers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get a list of authors, books, and or publishers", e1);
			throw new CriticalSQLException("Failed to a list of authors, books, and or publishers", e1);
		}
		
		println("Which book would you like to update?");
		printList(bookList);
		Book pickedBook = pickingFromAList(bookList);
		if(pickedBook == null) {
			return false;
		}
		
		println("Which author would you like to associate with this book?");
		printList(authorList);
		Author pickedAuthor = pickingFromAList(authorList);
		if(pickedAuthor == null) {
			return false;
		} else {
			pickedBook.setAuthor(pickedAuthor);
		}
		
		println("Which publisher would you like to associate with this book?");
		printList(publisherList);
		Publisher pickedPublisher = pickingFromAList(publisherList);
		if(pickedPublisher == null) {
			return false;
		} else {
			pickedBook.setPublisher(pickedPublisher);
		}
		
		println("What is the new title?");
		String choosenTitle = inStream.nextLine();
		pickedBook.setTitle(choosenTitle);
		try {
			adminService.updateBook(pickedBook);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to update book");
			println("We could not update your requested book");
			throw new CriticalSQLException("Failed to update a book", e);
		}
		return true;
	}
	
	private boolean updateToAuthor() throws CriticalSQLException {
		List<Author> authorList = new ArrayList<>();
		try {
			authorList = adminService.getAllAuthors();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all authors", e1);
			throw new CriticalSQLException("Failed to get all authors", e1);
		}

		println("Which author would you like to update?");
		printList(authorList);
		Author pickedAuthor = pickingFromAList(authorList);
		if(pickedAuthor == null) {
			return false;
		}

		println("What is the new name for this author?");
		String newName = inStream.nextLine();
		pickedAuthor.setName(newName);
		
		try {
			adminService.updateAuthor(pickedAuthor);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to update author");
			println("We could not update your requested author");
			throw new CriticalSQLException("Failed to update an author", e);
		}
		return true;
	}
	
	private boolean updateToPublisher() throws CriticalSQLException {
		List<Publisher> publisherList = new ArrayList<>();
		try {
			publisherList = adminService.getAllPublishers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all publishers", e1);
			throw new CriticalSQLException("Failed to get all publishers", e1);
		}

		println("Which publisher would you like to update?");
		printList(publisherList);
		Publisher pickedPublisher = pickingFromAList(publisherList);
		if(pickedPublisher == null) {
			return false;
		}

		println("What is the new name for this publisher?");
		String newName = inStream.nextLine();
		pickedPublisher.setName(newName);

		println("What is the new address for this publisher?");
		String newAddress = inStream.nextLine();
		pickedPublisher.setAddress(newAddress);

		println("What is the new phone for this publisher?");
		String newPhone = inStream.nextLine();
		pickedPublisher.setPhone(newPhone);
		
		try {
			adminService.updatePublisher(pickedPublisher);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to update publisher");
			println("We could not update your requested publisher");
			throw new CriticalSQLException("Failed to update publisher", e);
		}
		
		return true;
	}

	private boolean updateToBranch() throws CriticalSQLException {
		List<Branch> branchList = new ArrayList<>();
		try {
			branchList = adminService.getAllBranches();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all branches", e1);
			throw new CriticalSQLException("Failed to get all branches", e1);
		}

		println("Which branch would you like to update?");
		printList(branchList);
		Branch pickedbranch = pickingFromAList(branchList);
		if(pickedbranch == null) {
			return false;
		}

		println("What is the new name for this branch?");
		String newName = inStream.nextLine();
		pickedbranch.setName(newName);

		println("What is the new address for this branch?");
		String newAddress = inStream.nextLine();
		pickedbranch.setAddress(newAddress);
		
		try {
			adminService.updateBranch(pickedbranch);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to update branch");
			println("We could not update your requested library branch");
			throw new CriticalSQLException("Failed to update branch", e);
		}
		return true;
	}

	private boolean updateToBorrower() throws CriticalSQLException {
		List<Borrower> borrowerList = new ArrayList<>();
		try {
			borrowerList = adminService.getAllBorrowers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all borrowers", e1);
			throw new CriticalSQLException("Failed to get all borrowers", e1);
		}

		println("Which borrower would you like to update?");
		printList(borrowerList);
		Borrower pickedBorrower = pickingFromAList(borrowerList);
		if(pickedBorrower == null) {
			return false;
		}

		println("What is the new name for this borrower?");
		String newName = inStream.nextLine();
		pickedBorrower.setName(newName);

		println("What is the new address for this borrower?");
		String newAddress = inStream.nextLine();
		pickedBorrower.setAddress(newAddress);

		println("What is the new phone for this borrower?");
		String newPhone = inStream.nextLine();
		pickedBorrower.setPhone(newPhone);
		
		try {
			adminService.updateBorrower(pickedBorrower);
		} catch (UpdateException e) {
			LOGGER.log(Level.WARNING, "Failed to update borrower");
			println("We could not update your requested borrower");
			throw new CriticalSQLException("Failed to update borrower", e);
		}
		
		return true;
		
	}
	
	private boolean addingToADataBase() throws CriticalSQLException {
		boolean runAddingToDataBase = true;
		while(runAddingToDataBase) {
			println("Which database would you like to add to?");
			listOptionsForDataBases();
			try {
				int borrowerChoice = Integer.parseInt(inStream.nextLine());
				switch(borrowerChoice) {
				case 1:
					addingToBook();
					break;
				case 2:
					addingToAuthor();
					break;
				case 3:
					addingToPublisher();
					break;
				case 4:
					addingToBranch();
					break;
				case 5:
					addingToBorrower();
					break;
				case 6:
					runAddingToDataBase = false;
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
	
	private boolean addingToBook() throws CriticalSQLException {
		List<Author> authorList = new ArrayList<>();
		List<Publisher> publisherList = new ArrayList<>();
		try {
			authorList = adminService.getAllAuthors();
			publisherList = adminService.getAllPublishers();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all authors or publishers", e1);
			throw new CriticalSQLException("Failed to get all authors or publishers", e1);
		}
		
		println("Which author wrote this book?");
		printList(authorList);
		Author pickedAuthor = pickingFromAList(authorList);
		if(pickedAuthor == null) {
			return false;
		}
		
		println("Which publisher published this book?");
		printList(publisherList);
		Publisher pickedPublisher = pickingFromAList(publisherList);
		if(pickedPublisher == null) {
			return false;
		}
		
		println("What is the title?");
		String choosenTitle = inStream.nextLine();
		Book returntedBook = null;
		try {
			returntedBook = adminService.createBook(choosenTitle, pickedAuthor, pickedPublisher);
		} catch (InsertException e) {
			LOGGER.log(Level.WARNING, "Failed to create book");
			println("We could not create your requested book");
			throw new CriticalSQLException("Failed to create book", e);
		}

		if(returntedBook != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}
	
	private boolean addingToAuthor() throws CriticalSQLException {
		println("What is the name of the Author?");
		String newName = inStream.nextLine();
		Author newAuthor = null;
		try {
			newAuthor = adminService.createAuthor(newName);
		} catch (InsertException e) {
			LOGGER.log(Level.WARNING, "Failed to create author");
			println("We could not create your requested author");
			throw new CriticalSQLException("Failed to create author", e);
		}

		if(newAuthor != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}
	
	private boolean addingToPublisher() throws CriticalSQLException {
		println("What is the name of the publisher?");
		String newName = inStream.nextLine();
		println("What is the address of the publisher?");
		String newAddress = inStream.nextLine();
		println("What is the phone number for the publisher");
		String newPhone = inStream.nextLine();
		Publisher newPublisher = null;
		try {
			newPublisher = adminService.createPublisher(newName, newAddress, newPhone);
		} catch (InsertException e) {
			LOGGER.log(Level.WARNING, "Failed to create publisher");
			println("We could not create your requested publisher");
			throw new CriticalSQLException("Failed to create publisher", e);
		}

		if(newPublisher != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}

	private boolean addingToBranch() throws CriticalSQLException {
		println("What is the name of the library branch?");
		String newName = inStream.nextLine();
		println("What is the address of the library branch?");
		String newAddress = inStream.nextLine();
		Branch newBranch = null;
		try {
			newBranch = adminService.createBranch(newName, newAddress);
		} catch (InsertException e) {
			LOGGER.log(Level.WARNING, "Failed to create branch");
			println("We could not create your requested library branch");
			throw new CriticalSQLException("Failed to create branch", e);
		}

		if(newBranch != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}

	private boolean addingToBorrower() throws CriticalSQLException {
		println("What is the name of the borrower?");
		String newName = inStream.nextLine();
		println("What is the address of the borrower?");
		String newAddress = inStream.nextLine();
		println("What is the phone number for the borrower");
		String newPhone = inStream.nextLine();
		Borrower newBorrower = null;
		try {
			newBorrower = adminService.createBorrower(newName, newAddress, newPhone);
		} catch (InsertException e) {
			LOGGER.log(Level.WARNING, "Failed to create borrower");
			println("We could not create your requested borrower");
			throw new CriticalSQLException("Failed to create borrower", e);
		}

		if(newBorrower != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}
	
	/**
	 * return the T that the user picked, which can be null if user choice to exit
	 * 
	 * @param list of T values that the user choices from 
	 * @return T that the user choice
	 */
	private <T> T pickingFromAList(List<T> list) {
		T returnValue = null;
		boolean runUntilPicked = true;
		while(runUntilPicked) {
			try {
				int loc = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				loc--;
				if(loc == list.size()) {
					println("Back to Database Menu");
					runUntilPicked = false;
				} else if(loc < list.size() && loc >= 0) {
					returnValue = list.get(loc);
					runUntilPicked = false;
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return returnValue;
	}
	
	private void listOptionsForDataBases() {
		println("1) Book");
		println("2) Author");
		println("3) Publishers");
		println("4) Library Branches");
		println("5) Borrowers");
		println("6) Return to Admin Menu");
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
