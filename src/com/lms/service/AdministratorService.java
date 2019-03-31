package com.lms.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Publisher;

public interface AdministratorService extends Service {
	// for Book
	public abstract Book createBook(String title, Author author, Publisher publisher);
//	public abstract Book createBook(String, String author, String publisher); // not sure if this is necessary
	public abstract void updateBook(Book book);
	public abstract void deleteBook(Book book);
	public abstract List<Book> getAllBooks();

	// for Author
	public abstract Author createAuthor(String name);
	public abstract void updateAuthor(Author author);
	public abstract void deleteAuthor(Author author);
	public abstract List<Author> getAllAuthors();

	// for Publisher
	public abstract Publisher createPublisher(String name);
	public abstract Publisher createPublisher(String name, String address, String phone);
	public abstract void updatePublisher(Publisher publisher);
	public abstract void deletePublisher(Publisher publisher);
	public abstract List<Publisher> getAllPublishers();

	// for Library Branch
	public abstract Branch createBranch(String name , String address);
	public abstract void deleteBranch(Branch branch);
	public abstract void updateBranch(Branch branch);
	// get all branches is in the super interface

	// for Borrower
	public abstract Borrower createBorrower(String name, String address, String phone);
	public abstract void updateBorrower(Borrower borrower);
	public abstract void deleteBorrower(Borrower borrower);
	public abstract List<Borrower> getAllBorrowers();

	// Over-ride Due Date for a Book Loan
	// custom method that overrides the due date of a particular loaned book, if it exists, else returns false
	public abstract boolean overrideDueDateForLoan(Book book, Borrower borrower, Branch branch, LocalDate dueDate);

}
