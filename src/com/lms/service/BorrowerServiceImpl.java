package com.lms.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

public class BorrowerServiceImpl {
	private BorrowerDaoImpl borrowerDaoImpl;
	private BookLoansDaoImpl loanDaoImpl;
	private CopiesDaoImpl copiesDaoImpl;
	private LibraryBranchDaoImpl branchDaoImpl;
	private static final Logger LOGGER = Logger.getLogger(BorrowerDaoImpl.class.getName());

	public BorrowerServiceImpl(BorrowerDaoImpl borrowerDaoImpl, BookLoansDaoImpl loanDaoImpl,
			CopiesDaoImpl copiesDaoImpl, LibraryBranchDaoImpl branchDaoImpl) {
		this.borrowerDaoImpl = borrowerDaoImpl;
		this.loanDaoImpl = loanDaoImpl;
		this.copiesDaoImpl = copiesDaoImpl;
		this.branchDaoImpl = branchDaoImpl;
	}

	public Loan borrowBook(Borrower borrower, Book book, Branch branch, LocalDateTime dateOut, LocalDate dueDate) {
		Loan newLoan = null;
		try {
			newLoan = loanDaoImpl.create(book, borrower, branch, dateOut, dueDate);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to create a loan with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
						book.getId() + " and branch Id = " + branch.getId());
		}
		return newLoan;
	}

	public Map<Book, Integer> getAllBranchCopies(Branch branch) {
		Map<Book, Integer> listAllBranchCopies = new HashMap<>();
		try {
			listAllBranchCopies = copiesDaoImpl.getAllBranchCopies(branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all branch copies");
		}
		return listAllBranchCopies;
	}
	
	public boolean returnBook(Borrower borrower, Book book, Branch branch, LocalDate dueDate) {
		boolean returnedBook = false;
		Loan foundLoan = null;
		try {
			foundLoan = loanDaoImpl.get(book, borrower, branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get entry in loan with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
						book.getId() + " and branch Id = " + branch.getId());
		}

		if(foundLoan != null) {
			if(foundLoan.getDueDate().isAfter(dueDate)) {
				try {
					loanDaoImpl.delete(foundLoan);
					returnedBook = true;
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Failed to delete loan entry with Borrower CardNo = " + borrower.getCardNo() + " and book Id = " +
							book.getId() + " and branch Id = " + branch.getId());
				}
			}
		}
		return returnedBook;
	}
	
	public List<Branch> getAllBranchesWithLoan(Borrower borrower) {
		List<Branch> listOfAllBranchesWithLoan = new ArrayList<>();
		try {
			List<Loan> listOfAllLoans = loanDaoImpl.getAll();
			
			List<Loan> listOfAllLoansWithBorrower= listOfAllLoans.parallelStream().filter(l -> l.getBorrower().equals(borrower))
				.collect(Collectors.toList());

			listOfAllBranchesWithLoan = listOfAllLoansWithBorrower.parallelStream().map(l -> l.getBranch())
				.collect(Collectors.toList());
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all loans");
		}

		return listOfAllBranchesWithLoan;
	}
	
	public List<Loan> getAllBorrowedBooks(Borrower borrower) {
		List<Loan> listOfAllLoanFromBorrower = new ArrayList<>();
		try {
			List<Loan> listOfAllLoans = loanDaoImpl.getAll();
			
			listOfAllLoanFromBorrower = listOfAllLoans.parallelStream()
					.filter(l -> l.getBorrower().equals(borrower))
					.collect(Collectors.toList());
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get all loans");
		}
		
		return listOfAllLoanFromBorrower;
	}
	
	public Borrower getBorrower(int cardNo) {
		Borrower borrower = null;
		try {
			borrower = borrowerDaoImpl.get(cardNo);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get borrower with cardNo = " + cardNo);
		}
		return borrower;
	}
	

	public List<Branch> getAllBranches() {
		List<Branch> listOfBranches = new ArrayList<>();
		try {
			listOfBranches = branchDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to give a list of all branches in the branch table");
		}
		return listOfBranches;
	}
}
