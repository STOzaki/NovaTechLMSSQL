package com.lms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

public class AdministratorServiceImpl {
	private BookDaoImpl bookDaoImpl;
	private AuthorDaoImpl authorDaoImpl;
	private PublisherDaoImpl publisherDaoImpl;
	private LibraryBranchDaoImpl branchDaoImpl;
	private BorrowerDaoImpl borrowerDaoImpl;
	private BookLoansDaoImpl loanDaoImpl;
	private static final Logger LOGGER = Logger.getLogger(LibrarianServiceImpl.class.getName());

	public AdministratorServiceImpl(BookDaoImpl bookDaoImpl, AuthorDaoImpl authorDaoImpl,
			PublisherDaoImpl publisherDaoImpl, LibraryBranchDaoImpl branchDaoImpl,
			BorrowerDaoImpl borrowerDaoImpl, BookLoansDaoImpl loanDaoImpl) {
		this.bookDaoImpl = bookDaoImpl;
		this.authorDaoImpl = authorDaoImpl;
		this.publisherDaoImpl = publisherDaoImpl;
		this.branchDaoImpl = branchDaoImpl;
		this.borrowerDaoImpl = borrowerDaoImpl;
		this.loanDaoImpl = loanDaoImpl;
	}
	public Book createBook(String title, Author author, Publisher publisher) {
		Book book = null;
		try {
			book = bookDaoImpl.create(title, author, publisher);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create new book");
		}
		return book;
	}
	
	public void updateBook(Book book) {
		try {
			bookDaoImpl.update(book);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Book");
		}
	}
	

	public void deleteBook(Book book) {
		try {
			bookDaoImpl.delete(book);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Book");
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

	public Author createAuthor(String name) {
		Author author = null;
		try {
			author = authorDaoImpl.create(name);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Author");
		}
		return author;
	}

	public void updateAuthor(Author author) {
		try {
			authorDaoImpl.update(author);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update an Author");
		}
	}

	public void deleteAuthor(Author author) {
		try {
			authorDaoImpl.delete(author);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete an Author");
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
	public Publisher createPublisher(String name) {
		Publisher publisher = null;
		try {
			publisher = publisherDaoImpl.create(name, "", "");
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with just name");
		}
		return publisher;
	}

	public Publisher createPublisher(String name, String address, String phone) {
		Publisher publisher = null;
		try {
			publisher = publisherDaoImpl.create(name, address, phone);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create an Publisher with all attributes");
		}
		return publisher;
	}

	public void updatePublisher(Publisher publisher) {
		try {
			publisherDaoImpl.update(publisher);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update Publisher");
		}
	}

	public void deletePublisher(Publisher publisher) {
		try {
			publisherDaoImpl.delete(publisher);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Publisher");
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

	public Branch createBranch(String name, String address) {
		Branch branch = null;
		try {
			branch = branchDaoImpl.create(name, address);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create Branch");
		}
		return branch;
	}

	public void deleteBranch(Branch branch) {
		try {
			branchDaoImpl.delete(branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch");
		}
	}

	public void updateBranch(Branch branch) {
		try {
			branchDaoImpl.update(branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete Branch");
		}
	}

	public Borrower createBorrower(String name, String address, String phone) {
		Borrower borrower = null;
		try {
			borrower = borrowerDaoImpl.create(name, address, phone);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create a Borrower");
		}
		return borrower;
	}

	public void updateBorrower(Borrower borrower) {
		try {
			borrowerDaoImpl.update(borrower);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a Borrower");
		}
	}

	public void deleteBorrower(Borrower borrower) {
		try {
			borrowerDaoImpl.delete(borrower);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to delete a Borrower");
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

	public boolean overrideDueDateForLoan(Book book, Borrower borrower, Branch branch, LocalDate dueDate) {
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
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Failed to update due date on a loan");
			}
		}
		return success;
	}
}
