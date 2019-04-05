package com.lms.service;

import java.sql.SQLException;
import java.sql.Connection;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.RetrieveException;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.lms.service.util.ConnectingToDataBase;

public class BorrowerServiceImpl {
	private BorrowerDaoImpl borrowerDaoImpl;
	private BookLoansDaoImpl loanDaoImpl;
	private CopiesDaoImpl copiesDaoImpl;
	private LibraryBranchDaoImpl branchDaoImpl;
	private Connection conn;
	private static final Logger LOGGER = Logger.getLogger(BorrowerDaoImpl.class.getName());

	public BorrowerServiceImpl(String env) throws CriticalSQLException {
		this.conn = ConnectingToDataBase.connectingToDataBase(env);
		this.borrowerDaoImpl = new BorrowerDaoImpl(conn);
		this.loanDaoImpl = new BookLoansDaoImpl(conn);
		this.copiesDaoImpl = new CopiesDaoImpl(conn);
		this.branchDaoImpl = new LibraryBranchDaoImpl(conn);
	}

	public void closeConnection() {
		ConnectingToDataBase.closingConnection(conn);
	}

	public Loan borrowBook(Borrower borrower, Book book, Branch branch, LocalDateTime dateOut, LocalDate dueDate) throws InsertException, CriticalSQLException {
		Loan newLoan = null;
		try {
			newLoan = loanDaoImpl.create(book, borrower, branch, dateOut, dueDate);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create a loan with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
						book.getId() + " and branch Id = " + branch.getId(), e);
			rollingBack();
			throw new InsertException("Failed to create a loan in Book Loan", e);
		}
		return newLoan;
	}

	public Map<Book, Integer> getAllBranchCopies(Branch branch) throws RetrieveException {
		Map<Book, Integer> listAllBranchCopies = new HashMap<>();
		try {
			listAllBranchCopies = copiesDaoImpl.getAllBranchCopies(branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all branch copies", e);
			throw new RetrieveException("Failed to get all branches", e);
		}
		return listAllBranchCopies;
	}
	
	public boolean returnBook(Borrower borrower, Book book, Branch branch, LocalDate dueDate) throws DeleteException, RetrieveException, CriticalSQLException {
		boolean returnedBook = false;
		Loan foundLoan = null;
		try {
			foundLoan = loanDaoImpl.get(book, borrower, branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get entry in loan with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
						book.getId() + " and branch Id = " + branch.getId(), e);
			throw new RetrieveException("Failed to get a entry in loan", e);
		}

		if(foundLoan != null) {
			if(foundLoan.getDueDate().isAfter(dueDate)) {
				try {
					loanDaoImpl.delete(foundLoan);
					returnedBook = true;
					conn.commit();
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Failed to delete loan entry with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
							book.getId() + " and branch Id = " + branch.getId(), e);
					rollingBack();
					throw new DeleteException("Failed to delete book loan", e);
				}
			}
		}
		return returnedBook;
	}
	
	public List<Branch> getAllBranchesWithLoan(Borrower borrower) throws RetrieveException {
		List<Branch> listOfAllBranchesWithLoan = new ArrayList<>();
		try {
			List<Loan> listOfAllLoans = loanDaoImpl.getAll();
			
			List<Loan> listOfAllLoansWithBorrower= listOfAllLoans.parallelStream().filter(l -> l.getBorrower().equals(borrower))
				.collect(Collectors.toList());

			listOfAllBranchesWithLoan = listOfAllLoansWithBorrower.parallelStream().map(l -> l.getBranch())
				.collect(Collectors.toList());
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get a list of all branches the borrower borrowed from", e);
			throw new RetrieveException("Failed to get a list of all branches the borrower borrowed from", e);
		}

		return listOfAllBranchesWithLoan;
	}
	
	public List<Loan> getAllBorrowedBooks(Borrower borrower) throws RetrieveException {
		List<Loan> listOfAllLoanFromBorrower = new ArrayList<>();
		try {
			List<Loan> listOfAllLoans = loanDaoImpl.getAll();
			
			listOfAllLoanFromBorrower = listOfAllLoans.parallelStream()
					.filter(l -> l.getBorrower().equals(borrower))
					.collect(Collectors.toList());
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get a list of all loans the borrower borrowed", e);
			throw new RetrieveException("Failed to get a list of all loans the borrower borrowed", e);
		}
		
		return listOfAllLoanFromBorrower;
	}
	
	public Borrower getBorrower(int cardNo) throws RetrieveException {
		Borrower borrower = null;
		try {
			borrower = borrowerDaoImpl.get(cardNo);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get borrower with cardNo = " + cardNo, e);
			throw new RetrieveException("Failed to get a borrower", e);
		}
		return borrower;
	}
	

	public List<Branch> getAllBranches() throws RetrieveException {
		List<Branch> listOfBranches = new ArrayList<>();
		try {
			listOfBranches = branchDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to give a list of all branches in the branch table", e);
			throw new RetrieveException("Failed to get a list of all branches", e);
		}
		return listOfBranches;
	}
	
	private void rollingBack() throws CriticalSQLException {
		if (conn != null) {
            try {
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
                conn.rollback();
            } catch(SQLException excep) {
            	LOGGER.log(Level.WARNING, excep.getMessage() + " in this class: " + excep.getClass());
            	throw new CriticalSQLException("Rollback Failed", excep);
            }
		}
	}
}
