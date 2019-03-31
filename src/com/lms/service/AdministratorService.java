package com.lms.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Publisher;

/**
 * A service interface to ease the creation of a UI for administrative users.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface AdministratorService extends Service {
	/**
	 * Create a book with the given title, author, and publisher and store it in the
	 * database.
	 *
	 * @param title     the title of the book
	 * @param author    the author of the book
	 * @param publisher the publisher of the book
	 * @return the newly-created book
	 */
	public abstract Book createBook(String title, Author author, Publisher publisher);
//	/**
//	 * Create a book with the given title, author, and publisher and store it in the
//	 * database.
//	 *
//	 * @param title     the title of the book
//	 * @param author    the author of the book
//	 * @param publisher the publisher of the book
//	 * @return the newly-created book
//	 */
//	public abstract Book createBook(String, String author, String publisher); // not sure if this is necessary
	/**
	 * Update the database row representing the given book to match its current
	 * state.
	 *
	 * @param book the book to update in the database.
	 */
	public abstract void updateBook(Book book);
	/**
	 * Remove the given book from the database.
	 * @param book the book to remove
	 */
	public abstract void deleteBook(Book book);
	/**
	 * Get a list (order should not be relied on) of all the books in the database.
	 * @return all the books in the database.
	 */
	public abstract List<Book> getAllBooks();

	/**
	 * Create an author object and add the author to the database.
	 * @param name the name of the author
	 * @return the newly created Author object
	 */
	public abstract Author createAuthor(String name);

	/**
	 * Update the database record for the given author object to match its current
	 * state.
	 *
	 * @param author the author to update in the database.
	 */
	public abstract void updateAuthor(Author author);
	/**
	 * Remove the given author from the database.
	 * @param author the author to remove
	 */
	public abstract void deleteAuthor(Author author);
	/**
	 * Get a list (order should not be relied on) of all the authors in the database.
	 * @return all the authors in the database.
	 */
	public abstract List<Author> getAllAuthors();

	/**
	 * Create a publisher object, with no address or phone number, and add the
	 * publisher to the database.
	 *
	 * @param name the publisher's name
	 * @return the newly created publisher
	 */
	public abstract Publisher createPublisher(String name);

	/**
	 * Create a publisher object with full state and add the publisher to the
	 * database.
	 *
	 * @param name    the publisher's name
	 * @param address the publisher's address
	 * @param phone   the publisher's phone number
	 * @return the newly created publisher
	 */
	public abstract Publisher createPublisher(String name, String address, String phone);

	/**
	 * Update the database record representing the given publisher to match its
	 * state.
	 *
	 * @param publisher the publisher to update in the database
	 */
	public abstract void updatePublisher(Publisher publisher);
	/**
	 * Remove the given publisher from the database.
	 * @param publisher the publisher to remove.
	 */
	public abstract void deletePublisher(Publisher publisher);

	/**
	 * Get a list (order should not be relied on) of all the publishers in the
	 * database.
	 *
	 * @return all the publishers in the database.
	 */
	public abstract List<Publisher> getAllPublishers();

	/**
	 * Create a library branch object and add it to the database.
	 * @param name the name of the branch
	 * @param address the address of the branch
	 * @return the newly created branch object
	 */
	public abstract Branch createBranch(String name, String address);
	/**
	 * Remove the given branch from the database.
	 * @param branch the branch to remove
	 */
	public abstract void deleteBranch(Branch branch);
	/**
	 * Update the database row representing the given branch with its state.
	 * @param branch the branch to update in the database
	 */
	public abstract void updateBranch(Branch branch);
	// getAllBranches() is specified in the Service interface

	/**
	 * Create a Borrower object with the given properties and add the borrower to
	 * the database.
	 *
	 * @param name    the borrower's name
	 * @param address the borrower's address
	 * @param phone   the borrower's phone number
	 * @return the newly created borrower object
	 */
	public abstract Borrower createBorrower(String name, String address, String phone);

	/**
	 * Update the database row representing the given borrower with the object's
	 * state.
	 *
	 * @param borrower the borrower to update in the database
	 */
	public abstract void updateBorrower(Borrower borrower);
	/**
	 * Remove the given borrower from the database.
	 * @param borrower the borrower to remove
	 */
	public abstract void deleteBorrower(Borrower borrower);

	/**
	 * Get a list (order should not be relied on) of all the borrowers in the
	 * database.
	 *
	 * @return all the borrowers in the database.
	 */
	public abstract List<Borrower> getAllBorrowers();

	/**
	 * Override the due date for the given borrower's loan of the given book from
	 * the given branch, returning true on success and false if that borrower does
	 * not have that book out from that branch.
	 *
	 * @param book the book in question
	 * @param borrower the borrower in question
	 * @param branch the branch in question
	 * @param dueDate the new due date for the loan
	 * @return true on success, false if no such loan currently exists
	 */
	public abstract boolean overrideDueDateForLoan(Book book, Borrower borrower, Branch branch, LocalDate dueDate);
}
