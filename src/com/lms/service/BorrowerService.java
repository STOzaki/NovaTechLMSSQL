package com.lms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

/**
 * A service interface to ease the creation of a UI for borrowers (library
 * patrons).
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface BorrowerService extends Service {
	/**
	 * Create a new loan entry in the database representing the given borrower
	 * checking out the given book from the given branch.
	 *
	 * @param borrower the patron checking out the book
	 * @param book the book being checked out
	 * @param branch the branch from which the book is being borrowed
	 * @param dateOut the date the book is being checked out
	 * @param dueDate the date the book is due
	 * @return the object representing the loan
	 */
	Loan borrowBook(Borrower borrower, Book book, Branch branch, LocalDateTime dateOut, LocalDate dueDate) throws SQLException;

	/**
	 * Get all book-copy counts for the given branch.
	 *
	 * @param branch the branch in question
	 * @return a mapping from books in that library to the number of copies of each
	 *         that it has.
	 */
	Map<Book, Integer> getAllBranchCopies(Branch branch) throws SQLException;

	/**
	 * Handle a returned book: if there is an outstanding loan of the given book to
	 * the given borrower from the given branch, and the book is not overdue, remove
	 * the loan from the database and return true. If it is overdue, return false.
	 *
	 * <p>TODO: What to do when no matching loan exists?
	 *
	 * @param borrower the borrower returning the book
	 * @param book the book being returned
	 * @param branch the branch from which it was borrowed
	 * @param dueDate TODO: document this once author clarifies its purpose
	 * @return true on success, false if the book was overdue
	 */
	boolean returnBook(Borrower borrower, Book book, Branch branch, LocalDate dueDate) throws SQLException;

	/**
	 * Get all branches from which the borrower has an outstanding loan.
	 * 
	 * @param borrower in question
	 * @return all branches the borrower owes a book return to.
	 */
	List<Branch> getAllBranchesWithLoan(Borrower borrower) throws SQLException;

	/**
	 * Get all book loans the borrower has borrowed from any library branch.
	 * 
	 * @param borrower in question
	 * @return the list of book loans the borrower has out from any library.
	 */
	List<Loan> getAllBorrowedBooks(Borrower borrower) throws SQLException;

	/**
	 * Get Borrower with the specified cardNo
	 * 
	 * @param cardNo the borrower's cardNo
	 * @return Borrower or null if there is no Borrower with that cardNo
	 */
	Borrower getBorrower(int cardNo) throws SQLException;

}
