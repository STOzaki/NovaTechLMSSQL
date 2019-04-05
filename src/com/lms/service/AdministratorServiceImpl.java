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
import com.lms.customExceptions.RetrieveException;
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
	
	public void closeConnection() {
		ConnectingToDataBase.closingConnection(conn);
	}

	public Book createBook(String title, Author author, Publisher publisher) throws InsertException {
		Book book = null;
		try {
			book = bookDaoImpl.create(title, author, publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create new book", e);
			rollingBack();
			throw new InsertException("Failed to create a book", e);
		}
		return book;
	}
	
	public void updateBook(Book book) throws UpdateException {
		try {
			bookDaoImpl.update(book);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Book", e);
			rollingBack();
			throw new UpdateException("Failed to update book", e);
		}
	}
	

	public void deleteBook(Book book) throws DeleteException {
		try {
			bookDaoImpl.delete(book);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Book", e);
			rollingBack();
			throw new DeleteException("Failed to delete Book", e);
		}
	}
	
	public List<Book> getAllBooks() throws RetrieveException {
		List<Book> listOfAllBooks = new ArrayList<>();
		try {
			listOfAllBooks = bookDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Book", e);
			throw new RetrieveException("Failed to get all books", e);
		}
		return listOfAllBooks;
	}

	public Author createAuthor(String name) throws InsertException {
		Author author = null;
		try {
			author = authorDaoImpl.create(name);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Author", e);
			rollingBack();
			throw new InsertException("Failed to create author", e);
		}
		return author;
	}

	public void updateAuthor(Author author) throws UpdateException {
		try {
			authorDaoImpl.update(author);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update an Author", e);
			rollingBack();
			throw new UpdateException("Failed to update author", e);
		}
	}

	public void deleteAuthor(Author author) throws DeleteException {
		try {
			authorDaoImpl.delete(author);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete an Author", e);
			rollingBack();
			throw new DeleteException("Failed to delete author", e);
		}
	}

	public List<Author> getAllAuthors() throws RetrieveException {
		List<Author> listOfAllAuthors = new ArrayList<>();
		try {
			listOfAllAuthors = authorDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Authors", e);
			throw new RetrieveException("Failed to get all authors", e);
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
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with just name", e);
			rollingBack();
			throw new InsertException("Failed to create publisher", e);
		}
		return publisher;
	}

	public Publisher createPublisher(String name, String address, String phone) throws InsertException {
		Publisher publisher = null;
		try {
			publisher = publisherDaoImpl.create(name, address, phone);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with all attributes", e);
			rollingBack();
			throw new InsertException("Failed to create publisher", e);
		}
		return publisher;
	}

	public void updatePublisher(Publisher publisher) throws UpdateException {
		try {
			publisherDaoImpl.update(publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Publisher", e);
			rollingBack();
			throw new UpdateException("Failed to update publisher", e);
		}
	}

	public void deletePublisher(Publisher publisher) throws DeleteException {
		try {
			publisherDaoImpl.delete(publisher);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Publisher", e);
			rollingBack();
			throw new DeleteException("Failed to delete publisher", e);
		}
	}

	public List<Publisher> getAllPublishers() throws RetrieveException {
		List<Publisher> listOfAllPublishers = new ArrayList<>();
		try {
			listOfAllPublishers = publisherDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Publisher", e);
			throw new RetrieveException("Failed to get all publishers", e);
		}
		return listOfAllPublishers;
	}

	public Branch createBranch(String name, String address) throws InsertException {
		Branch branch = null;
		try {
			branch = branchDaoImpl.create(name, address);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create Branch", e);
			rollingBack();
			throw new InsertException("Failed to create branch", e);
		}
		return branch;
	}

	public void deleteBranch(Branch branch) throws DeleteException {
		try {
			branchDaoImpl.delete(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch", e);
			rollingBack();
			throw new DeleteException("Failed to delete branch", e);
		}
	}

	public void updateBranch(Branch branch) throws UpdateException {
		try {
			branchDaoImpl.update(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch", e);
			rollingBack();
			throw new UpdateException("Failed to update branch", e);
		}
	}

	public Borrower createBorrower(String name, String address, String phone) throws InsertException {
		Borrower borrower = null;
		try {
			borrower = borrowerDaoImpl.create(name, address, phone);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create a Borrower", e);
			rollingBack();
			throw new InsertException("Failed to create borrower", e);
		}
		return borrower;
	}

	public void updateBorrower(Borrower borrower) throws UpdateException {
		try {
			borrowerDaoImpl.update(borrower);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a Borrower", e);
			rollingBack();
			throw new UpdateException("Failed to update borrower", e);
		}
	}

	public void deleteBorrower(Borrower borrower) throws DeleteException {
		try {
			borrowerDaoImpl.delete(borrower);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete a Borrower", e);
			rollingBack();
			throw new DeleteException("Failed to delete a borrower", e);
		}
	}

	public List<Borrower> getAllBorrowers() throws RetrieveException {
		List<Borrower> listOfAllBorrowers = new ArrayList<>();
		try {
			listOfAllBorrowers = borrowerDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Borrower", e);
			throw new RetrieveException("Failed to get all Borrowers", e);
		}
		return listOfAllBorrowers;
	}

	public List<Loan> getAllLoans() throws RetrieveException {
		List<Loan> listOfAllLoans = new ArrayList<>();
		try {
			listOfAllLoans = loanDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Book Loans", e);
			throw new RetrieveException("Failed to get all loans", e);
		}
		return listOfAllLoans;
	}

	public List<Branch> getAllBranches() throws RetrieveException {
		List<Branch> listOfAllBranches = new ArrayList<>();
		try {
			listOfAllBranches = branchDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all Branches", e);
			throw new RetrieveException("Failed to get all branches", e);
		}
		return listOfAllBranches;
	}

	public boolean overrideDueDateForLoan(Book book, Borrower borrower, Branch branch, LocalDate dueDate) throws UpdateException, RetrieveException {
		boolean success = false;
		Loan foundLoan = null;
		try {
			foundLoan = loanDaoImpl.get(book, borrower, branch);
		} catch (SQLException e1) {
			LOGGER.log(Level.WARNING, "Failed to get the Book Loans", e1);
			throw new RetrieveException("Failed to get a book loan", e1);
		}

		if(foundLoan != null) {
			foundLoan.setDueDate(dueDate);
			try {
				loanDaoImpl.update(foundLoan);
				success = true;
				conn.commit();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Failed to update due date on a loan", e);
				rollingBack();
				throw new UpdateException("Failed to update due date on loan", e);
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
