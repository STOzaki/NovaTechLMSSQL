package com.lms.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.dao.PublisherDaoImpl;
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
	
	public AdminMenu(Connection conn, Scanner inStream, Appendable outStream) {
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

		adminService = new AdministratorServiceImpl(new BookDaoImpl(conn), new AuthorDaoImpl(conn),
				new PublisherDaoImpl(conn), new LibraryBranchDaoImpl(conn),
				new BorrowerDaoImpl(conn), new BookLoansDaoImpl(conn), conn);	
	}
	
	public boolean start() {
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
	}

	private boolean overrideDueDate() {
		List<Loan> listOfLoans = adminService.getAllLoans();
		
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
		adminService.overrideDueDateForLoan(loanToOverride.getBook(), loanToOverride.getBorrower(),
				loanToOverride.getBranch(), newDueDate);
		return true;
	}
	
	private boolean deleteToADataBase() {
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
	
	private boolean deleteToBook() {
		List<Book> bookList = adminService.getAllBooks();
		println("Which book would you like to delete?");
		printList(bookList);
		Book deletingBook = pickingFromAList(bookList);
		if(deletingBook == null) {
			return false;
		}
		adminService.deleteBook(deletingBook);
		return true;
	}

	private boolean deleteToAuthor() {
		List<Author> authorList = adminService.getAllAuthors();
		println("Which author would you like to delete?");
		printList(authorList);
		Author deletingAuthor = pickingFromAList(authorList);
		if(deletingAuthor == null) {
			return false;
		}
		adminService.deleteAuthor(deletingAuthor);
		return true;
	}

	private boolean deleteToPublisher() {
		List<Publisher> publisherList = adminService.getAllPublishers();
		println("Which publisher would you like to delete?");
		printList(publisherList);
		Publisher deletingPublisher = pickingFromAList(publisherList);
		if(deletingPublisher == null) {
			return false;
		}
		adminService.deletePublisher(deletingPublisher);
		return true;
	}

	private boolean deleteToBranch() {
		List<Branch> branchList = adminService.getAllBranches();
		println("Which branch would you like to delete?");
		printList(branchList);
		Branch deletingBranch = pickingFromAList(branchList);
		if(deletingBranch == null) {
			return false;
		}
		adminService.deleteBranch(deletingBranch);
		return true;
	}

	private boolean deleteToBorrower() {
		List<Borrower> borrowerList = adminService.getAllBorrowers();
		println("Which borrower would you like to delete?");
		printList(borrowerList);
		Borrower deletingBorrower = pickingFromAList(borrowerList);
		if(deletingBorrower == null) {
			return false;
		}
		adminService.deleteBorrower(deletingBorrower);
		return true;
	}
	
	private boolean updateToADataBase() {
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
	
	private boolean updateToBook() {
		List<Book> bookList = adminService.getAllBooks();
		List<Author> authorList = adminService.getAllAuthors();
		List<Publisher> publisherList = adminService.getAllPublishers();
		
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
		adminService.updateBook(pickedBook);
		return true;
	}
	
	private boolean updateToAuthor() {
		List<Author> authorList = adminService.getAllAuthors();

		println("Which author would you like to update?");
		printList(authorList);
		Author pickedAuthor = pickingFromAList(authorList);
		if(pickedAuthor == null) {
			return false;
		}

		println("What is the new name for this author?");
		String newName = inStream.nextLine();
		pickedAuthor.setName(newName);
		
		adminService.updateAuthor(pickedAuthor);
		return true;
	}
	
	private boolean updateToPublisher() {
		List<Publisher> publisherList = adminService.getAllPublishers();

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
		
		adminService.updatePublisher(pickedPublisher);
		
		return true;
	}

	private boolean updateToBranch() {
		List<Branch> branchList = adminService.getAllBranches();

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
		
		adminService.updateBranch(pickedbranch);
		return true;
	}

	private boolean updateToBorrower() {
		List<Borrower> borrowerList = adminService.getAllBorrowers();

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
		
		adminService.updateBorrower(pickedBorrower);
		
		return true;
		
	}
	
	private boolean addingToADataBase() {
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
	
	private boolean addingToBook() {
		List<Author> authorList = adminService.getAllAuthors();
		List<Publisher> publisherList = adminService.getAllPublishers();
		
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
		Book returntedBook = adminService.createBook(choosenTitle, pickedAuthor, pickedPublisher);
		if(returntedBook != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}
	
	private boolean addingToAuthor() {
		println("What is the name of the Author?");
		String newName = inStream.nextLine();
		Author newAuthor = adminService.createAuthor(newName);
		if(newAuthor != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}
	
	private boolean addingToPublisher() {
		println("What is the name of the publisher?");
		String newName = inStream.nextLine();
		println("What is the address of the publisher?");
		String newAddress = inStream.nextLine();
		println("What is the phone number for the publisher");
		String newPhone = inStream.nextLine();
		Publisher newPublisher = adminService.createPublisher(newName, newAddress, newPhone);
		if(newPublisher != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}

	private boolean addingToBranch() {
		println("What is the name of the library branch?");
		String newName = inStream.nextLine();
		println("What is the address of the library branch?");
		String newAddress = inStream.nextLine();
		Branch newBranch = adminService.createBranch(newName, newAddress);
		if(newBranch != null) {
			println("Added successfully");
		} else {
			println("Unsuccessful to add");
		}
		return true;
	}

	private boolean addingToBorrower() {
		println("What is the name of the borrower?");
		String newName = inStream.nextLine();
		println("What is the address of the borrower?");
		String newAddress = inStream.nextLine();
		println("What is the phone number for the borrower");
		String newPhone = inStream.nextLine();
		Borrower newBorrower = adminService.createBorrower(newName, newAddress, newPhone);
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
