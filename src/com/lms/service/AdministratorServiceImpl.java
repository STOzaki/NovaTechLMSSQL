package com.lms.service;

import java.sql.SQLException;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.LocalDate;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.UpdateException;
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

import com.lms.service.util.ConnectingToDataBase;

public class AdministratorServiceImpl {
	private BookDaoImpl bookDaoImpl;
	private AuthorDaoImpl authorDaoImpl;
	private PublisherDaoImpl publisherDaoImpl;
	private LibraryBranchDaoImpl branchDaoImpl;
	private BorrowerDaoImpl borrowerDaoImpl;
	private BookLoansDaoImpl loanDaoImpl;
	private Connection conn;
	private static final Logger LOGGER = Logger.getLogger(AdministratorServiceImpl.class.getName());

	public AdministratorServiceImpl(String env) throws CriticalSQLException {
		this.conn = ConnectingToDataBase.connectingToDataBase(env);
		this.bookDaoImpl = new BookDaoImpl(conn);
		this.authorDaoImpl = new AuthorDaoImpl(conn);
		this.publisherDaoImpl = new PublisherDaoImpl(conn);
		this.branchDaoImpl = new LibraryBranchDaoImpl(conn);
		this.borrowerDaoImpl = new BorrowerDaoImpl(conn);
		this.loanDaoImpl = new BookLoansDaoImpl(conn);
	}

	public Book createBook(String title, Author author, Publisher publisher) throws InsertException {
		Book book = null;
		try {
			book = bookDaoImpl.create(title, author, publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create new book");
			rollingBack();
			throw new InsertException("Failed to create a book");
		}
		return book;
	}
	
	public void updateBook(Book book) throws UpdateException {
		try {
			bookDaoImpl.update(book);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Book");
			rollingBack();
			throw new UpdateException("Failed to update book");
		}
	}
	

	public void deleteBook(Book book) throws DeleteException {
		try {
			bookDaoImpl.delete(book);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Book");
			rollingBack();
			throw new DeleteException("Failed to delete Book");
		}
	}
	
	public List<Book> getAllBooks() {
		List<Book> listOfAllBooks = new ArrayList<>();
		try {
			listOfAllBooks = bookDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Book");
		}
		return listOfAllBooks;
	}

	public Author createAuthor(String name) throws InsertException {
		Author author = null;
		try {
			author = authorDaoImpl.create(name);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Author");
			rollingBack();
			throw new InsertException("Failed to create author");
		}
		return author;
	}

	public void updateAuthor(Author author) throws UpdateException {
		try {
			authorDaoImpl.update(author);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update an Author");
			rollingBack();
			throw new UpdateException("Failed to update author");
		}
	}

	public void deleteAuthor(Author author) throws DeleteException {
		try {
			authorDaoImpl.delete(author);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete an Author");
			rollingBack();
			throw new DeleteException("Failed to delete author");
		}
	}

	public List<Author> getAllAuthors() {
		List<Author> listOfAllAuthors = new ArrayList<>();
		try {
			listOfAllAuthors = authorDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Authors");
		}
		return listOfAllAuthors;
	}

	// publisher should not have null columns
	public Publisher createPublisher(String name) throws InsertException {
		Publisher publisher = null;
		try {
			publisher = publisherDaoImpl.create(name, "", "");
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with just name");
			rollingBack();
			throw new InsertException("Failed to create publisher");
		}
		return publisher;
	}

	public Publisher createPublisher(String name, String address, String phone) throws InsertException {
		Publisher publisher = null;
		try {
			publisher = publisherDaoImpl.create(name, address, phone);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with all attributes");
			rollingBack();
			throw new InsertException("Failed to create publisher");
		}
		return publisher;
	}

	public void updatePublisher(Publisher publisher) throws UpdateException {
		try {
			publisherDaoImpl.update(publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Publisher");
			rollingBack();
			throw new UpdateException("Failed to update publisher");
		}
	}

	public void deletePublisher(Publisher publisher) throws DeleteException {
		try {
			publisherDaoImpl.delete(publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Publisher");
			rollingBack();
			throw new DeleteException("Failed to delete publisher");
		}
	}

	public List<Publisher> getAllPublishers() {
		List<Publisher> listOfAllPublishers = new ArrayList<>();
		try {
			listOfAllPublishers = publisherDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Publisher");
		}
		return listOfAllPublishers;
	}

	public Branch createBranch(String name, String address) throws InsertException {
		Branch branch = null;
		try {
			branch = branchDaoImpl.create(name, address);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create Branch");
			rollingBack();
			throw new InsertException("Failed to create branch");
		}
		return branch;
	}

	public void deleteBranch(Branch branch) throws DeleteException {
		try {
			branchDaoImpl.delete(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch");
			rollingBack();
			throw new DeleteException("Failed to delete branch");
		}
	}

	public void updateBranch(Branch branch) throws UpdateException {
		try {
			branchDaoImpl.update(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch");
			rollingBack();
			throw new UpdateException("Failed to update branch");
		}
	}

	public Borrower createBorrower(String name, String address, String phone) throws InsertException {
		Borrower borrower = null;
		try {
			borrower = borrowerDaoImpl.create(name, address, phone);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create a Borrower");
			rollingBack();
			throw new InsertException("Failed to create borrower");
		}
		return borrower;
	}

	public void updateBorrower(Borrower borrower) throws UpdateException {
		try {
			borrowerDaoImpl.update(borrower);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a Borrower");
			rollingBack();
			throw new UpdateException("Failed to update borrower");
		}
	}

	public void deleteBorrower(Borrower borrower) throws DeleteException {
		try {
			borrowerDaoImpl.delete(borrower);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete a Borrower");
			rollingBack();
			throw new DeleteException("Failed to delete a borrower");
		}
	}

	public List<Borrower> getAllBorrowers() {
		List<Borrower> listOfAllBorrowers = new ArrayList<>();
		try {
			listOfAllBorrowers = borrowerDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Borrower");
		}
		return listOfAllBorrowers;
	}

	public List<Loan> getAllLoans() {
		List<Loan> listOfAllLoans = new ArrayList<>();
		try {
			listOfAllLoans = loanDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Book Loans");
		}
		return listOfAllLoans;
	}

	public List<Branch> getAllBranches() {
		List<Branch> listOfAllBranches = new ArrayList<>();
		try {
			listOfAllBranches = branchDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Branches");
		}
		return listOfAllBranches;
	}

	public boolean overrideDueDateForLoan(Book book, Borrower borrower, Branch branch, LocalDate dueDate) throws UpdateException {
		boolean success = false;
		Loan foundLoan = null;
		try {
			foundLoan = loanDaoImpl.get(book, borrower, branch);
		} catch (SQLException e1) {
			LOGGER.log(Level.WARNING, "Failed to get the Book Loans");
		}

		if(foundLoan != null) {
			foundLoan.setDueDate(dueDate);
			try {
				loanDaoImpl.update(foundLoan);
				success = true;
				conn.commit();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Failed to update due date on a loan");
				rollingBack();
				throw new UpdateException("Failed to update due date on loan");
			}
		}
		return success;
	}
	
	private void rollingBack() {
		if (conn != null) {
            try {
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
                conn.rollback();
            } catch(SQLException excep) {
            	LOGGER.log(Level.WARNING, excep.getMessage() + " in this class: " + excep.getClass());
            }
		}
	}
}
