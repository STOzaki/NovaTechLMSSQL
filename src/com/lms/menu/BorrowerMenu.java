package com.lms.menu;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.RetrieveException;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.BorrowerServiceImpl;

public class BorrowerMenu {
	private BorrowerServiceImpl borrowerService;
	
	private final Scanner inStream;
	private final Appendable outStream;

	private static final Logger LOGGER = Logger.getLogger(BorrowerMenu.class.getName());

	public BorrowerMenu(Scanner inStream, Appendable outStream) throws CriticalSQLException {
		this.inStream = inStream;
		this.outStream = outStream;
	}
	
	public boolean start() throws CriticalSQLException {
		try {
			borrowerService = new BorrowerServiceImpl("production");
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
				} catch (RetrieveException e) {
					LOGGER.log(Level.WARNING, "Failed to get a borrower from ther borrower service", e);
					throw new CriticalSQLException("Failed to get a borrower from ther borrower service", e);
				}
			}
			return true;
		} catch (CriticalSQLException e) {
			throw new CriticalSQLException("Internal service error with the borrower service", e);
		} finally {
			try {
				borrowerService.closeConnection();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to close connection");
			}
		}
	}
	
	private boolean accessGrantedToBorrower(Borrower borrower) throws CriticalSQLException {
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
	
	private boolean returnABook(Borrower borrower) throws CriticalSQLException {
		println("Pick the Branch you want to check out from:");
		List<Branch> listOfAllBranches = new ArrayList<>();
		try {
			listOfAllBranches = borrowerService.getAllBranches();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all branches", e1);
			throw new CriticalSQLException("Failed to get all branches", e1);
		}
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
	
	private boolean pickingBookToReturn(Borrower borrower, Branch branch) throws CriticalSQLException {
		List<Loan> BooksLoanedToBorrower = new ArrayList<>();
		try {
			BooksLoanedToBorrower = borrowerService.getAllBorrowedBooks(borrower);
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all borrowed books", e1);
			throw new CriticalSQLException("Failed to get all borrowed books", e1);
		}
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
			} catch (DeleteException e) {
				LOGGER.log(Level.WARNING, "Failed to return a book");
				println("Unfortunatly, we were unable to return your book at this time.");
				throw new CriticalSQLException("Failed to return a book", e);
			} catch (RetrieveException e) {
				LOGGER.log(Level.WARNING, "Failed to return a book", e);
				throw new CriticalSQLException("Failed to return a book", e);
			}
		}
		return true;
		
	}
	
	private boolean checkOutBookFromBranch(Borrower borrower) throws CriticalSQLException {
		println("Pick the Branch you want to check out from:");
		List<Branch> listOfAllBranches = new ArrayList<>();
		try {
			listOfAllBranches = borrowerService.getAllBranches();
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all branches", e1);
			throw new CriticalSQLException("Failed to get all branches", e1);
		}
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
	
	private boolean pickBookToCheckOut(Borrower borrower, Branch branch) throws CriticalSQLException {
		Map<Book, Integer> mapOfAllBooksInBranch = new HashMap<>();
		try {
			mapOfAllBooksInBranch = borrowerService.getAllBranchCopies(branch);
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all branch copies", e1);
			throw new CriticalSQLException("Failed to get all branch copies", e1);
		}
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
	
	private boolean borrowingABook(Borrower borrower, Branch branch, Book book) throws CriticalSQLException {
		List<Loan> listOfAllLoansFromBorrower = new ArrayList<>();
		try {
			listOfAllLoansFromBorrower = borrowerService.getAllBorrowedBooks(borrower);
		} catch (RetrieveException e1) {
			LOGGER.log(Level.WARNING, "Failed to get all borrowed books", e1);
			throw new CriticalSQLException("Failed to get borrowed books", e1);
		}
		List<Loan> matchingListOfLoans = listOfAllLoansFromBorrower.parallelStream().filter(l -> l.getBook().equals(book) &&
				l.getBorrower().equals(borrower) && l.getBranch().equals(branch)).collect(Collectors.toList());
		// it found a loan that matched the primary Id
		if(matchingListOfLoans.size() > 0) {
			println("I am sorry but you have already borrowed that book: " + book.getTitle() + " from " + 
					branch.getName() + " on " + matchingListOfLoans.get(0).getDateOut());
		} else if(matchingListOfLoans.size() == 0) {
			try {
				borrowerService.borrowBook(borrower, book, branch, LocalDateTime.now(), LocalDate.now().plusWeeks(1));
			} catch (InsertException e) {
				LOGGER.log(Level.WARNING, "Failed to borrower a book");
				println("We were unable to borrower the requested book");
				throw new CriticalSQLException("Failed to borrow a book", e);
			}
		}
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
