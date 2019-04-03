package com.lms.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
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
	
	public BorrowerMenu(Connection conn, Scanner inStream, Appendable outStream) {
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
		
		borrowerService = new BorrowerServiceImpl(new BorrowerDaoImpl(conn), new BookLoansDaoImpl(conn),
				new CopiesDaoImpl(conn), new LibraryBranchDaoImpl(conn), conn);
	}
	
	public boolean start() {
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
