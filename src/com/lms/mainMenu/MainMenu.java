package com.lms.mainMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.dao.PublisherDaoImpl;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.model.Publisher;
import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;
import com.lms.service.LibrarianServiceImpl;

public class MainMenu {
	
	private BorrowerServiceImpl borrowerService;
	private AdministratorServiceImpl adminService;
	private LibrarianServiceImpl libraryService;
	
	private final Scanner inStream;
	private final Appendable outStream;
	
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
		
		borrowerService = new BorrowerServiceImpl(new BorrowerDaoImpl(conn), new BookLoansDaoImpl(conn),
				new CopiesDaoImpl(conn), new LibraryBranchDaoImpl(conn));
		adminService = new AdministratorServiceImpl(new BookDaoImpl(conn), new AuthorDaoImpl(conn),
				new PublisherDaoImpl(conn), new LibraryBranchDaoImpl(conn),
				new BorrowerDaoImpl(conn), new BookLoansDaoImpl(conn));
		libraryService = new LibrarianServiceImpl(new LibraryBranchDaoImpl(conn), new BookDaoImpl(conn),
				new CopiesDaoImpl(conn));
		
	}

	public void start() {
		boolean run = true;
		while(run) {
			println("What kind of user are you? ((A)dmin, (L)ibrarian, or (B)orrower) or (Q)uit");
			String userChoice = inStream.nextLine();
			switch(userChoice) {
			case "A": case "a": case "Admin":
				theAdmin();
				break;
			case "L": case "l": case "Librarian":
				theLibrarian();
				break;
			case "B": case "b": case "Borrower":
				theBorrower();
				break;
			case "Q": case "q": case "Quit":
				run = false;
				break;
			default:
				println("I am sorry but that is not a user type. Please try again.");
			}
		}
	}
	
	private boolean theAdmin() {
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
	
	private boolean theBorrower() {
		boolean borrowerRun = true;
		while(borrowerRun) {
			println("Enter the your Card Number: (or 'quit' to exit.)");
			try {
				String answer = inStream.nextLine();
				if(answer.equals("quit")) {
					borrowerRun = false;
				} else {
					int theirCardNo = Integer.parseInt(answer);
					Borrower foundBorrower = borrowerService.getBorrower(theirCardNo);
					if(foundBorrower != null) {
						accessGrantedToBorrower(foundBorrower);
					} else {
						println("Access Denied. That is not a valid cardno.");
					}
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean accessGrantedToBorrower(Borrower borrower) {
		boolean accessRun = true;
		while(accessRun) {
			println("Choose an option:");
			println("1)Check out a book");
			println("2)Return a Book");
			println("3)Quit to Previous");
			try {
				int borrowerChoice = Integer.parseInt(inStream.nextLine());
				switch(borrowerChoice) {
				case 1:
					checkOutBookFromBranch(borrower);
					break;
				case 2:
					returnABook(borrower);
					break;
				case 3:
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
	
	private boolean returnABook(Borrower borrower) {
		println("Pick the Branch you want to check out from:");
		List<Branch> listOfAllBranches = borrowerService.getAllBranches();
		printList(listOfAllBranches);
		boolean runReturningABook = true;
		while(runReturningABook) {
			try {
				int branchNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				branchNum--;
				if(branchNum == listOfAllBranches.size()) {
					println("Back to Borrower Menu");
					runReturningABook = false;
				} else if(branchNum < listOfAllBranches.size() && branchNum >= 0) {
					pickingBookToReturn(borrower, listOfAllBranches.get(branchNum));
					runReturningABook = false;
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean pickingBookToReturn(Borrower borrower, Branch branch) {
		List<Loan> BooksLoanedToBorrower = borrowerService.getAllBorrowedBooks(borrower);
		List<Loan> BooksLoanedToBorrowerFromBranch = BooksLoanedToBorrower.parallelStream()
				.filter(l -> l.getBranch().equals(branch)).collect(Collectors.toList());
		printList(BooksLoanedToBorrowerFromBranch);
		boolean runPickingBookToReturn = true;
		while(runPickingBookToReturn) {
			try {
				int bookLoanedNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				bookLoanedNum--;
				if(bookLoanedNum == BooksLoanedToBorrowerFromBranch.size()) {
					println("Back to Borrower Menu");
					runPickingBookToReturn = false;
				} else if(bookLoanedNum < BooksLoanedToBorrowerFromBranch.size() && bookLoanedNum >= 0) {
					Loan loanWantToReturn = BooksLoanedToBorrowerFromBranch.get(bookLoanedNum);
					Book returningBook = BooksLoanedToBorrowerFromBranch.get(bookLoanedNum).getBook();
					boolean successOfReturn = borrowerService.returnBook(borrower,
							returningBook, branch, LocalDate.now());
					if(successOfReturn) {
						println("Successfully returned");
					} else {
						println("Unfortunatly, you have returned " + returningBook.getTitle() + " too late");
						println("That was due on " + loanWantToReturn);
					}
					runPickingBookToReturn = false;
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
		
	}
	
	private boolean checkOutBookFromBranch(Borrower borrower) {
		println("Pick the Branch you want to check out from:");
		List<Branch> listOfAllBranches = borrowerService.getAllBranches();
		printList(listOfAllBranches);
		boolean runCheckingBranch = true;
		while(runCheckingBranch) {
			try {
				int branchNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				branchNum--;
				if(branchNum == listOfAllBranches.size()) {
					println("Back to Borrower Menu");
					runCheckingBranch = false;
				} else if(branchNum < listOfAllBranches.size() && branchNum >= 0) {
					pickBookToCheckOut(borrower, listOfAllBranches.get(branchNum));
					runCheckingBranch = false;
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean pickBookToCheckOut(Borrower borrower, Branch branch) {
		Map<Book, Integer> mapOfAllBooksInBranch = borrowerService.getAllBranchCopies(branch);
		List<Book>listOfAllBooksInBranch = new ArrayList<>(mapOfAllBooksInBranch.keySet());
		printList(listOfAllBooksInBranch);
		boolean runPickingABookToCheckOut = true;
		while(runPickingABookToCheckOut) {
			try {
				int bookNum = Integer.parseInt(inStream.nextLine());
				// -1 to keep in the range of the list (because the display will start from 1 not 0)
				bookNum--;
				if(bookNum == listOfAllBooksInBranch.size()) {
					println("Back to Borrower Menu");
					runPickingABookToCheckOut = false;
				} else if(bookNum < listOfAllBooksInBranch.size() && bookNum >= 0) {
					borrowingABook(borrower, branch, listOfAllBooksInBranch.get(bookNum));
					runPickingABookToCheckOut = false;
				} else {
					println("That is not an option");
				}
			} catch (NumberFormatException e) {
				println("That is not a number");
			}
		}
		return true;
	}
	
	private boolean borrowingABook(Borrower borrower, Branch branch, Book book) {
		List<Loan> listOfAllLoansFromBorrower = borrowerService.getAllBorrowedBooks(borrower);
		List<Loan> matchingListOfLoans = listOfAllLoansFromBorrower.parallelStream().filter(l -> l.getBook().equals(book) &&
				l.getBorrower().equals(borrower) && l.getBranch().equals(branch)).collect(Collectors.toList());
		// it found a loan that matched the primary Id
		if(matchingListOfLoans.size() > 0) {
			println("I am sorry but you have already borrowed that book: " + book.getTitle() + " from " + 
					branch.getName() + " on " + matchingListOfLoans.get(0).getDateOut());
		} else if(matchingListOfLoans.size() == 0) {
			borrowerService.borrowBook(borrower, book, branch, LocalDateTime.now(), LocalDate.now().plusWeeks(1));
		}
		return true;
	}
	
	private boolean theLibrarian() {
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
		
		libraryService.updateBranch(branch);
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
