package com.lms.service;

import java.sql.SQLException;
import java.sql.Connection;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.model.Book;
import com.lms.model.Branch;

import com.lms.service.util.ConnectingToDataBase;

public class LibrarianServiceImpl implements LibrarianService {
	private LibraryBranchDaoImpl branchDaoImpl;
	private BookDaoImpl bookDaoImpl;
	private CopiesDaoImpl copiesDaoImpl;
	private Connection conn;
	private static final Logger LOGGER = Logger.getLogger(LibrarianServiceImpl.class.getName());

	public LibrarianServiceImpl(String env) throws CriticalSQLException {
		this.conn = ConnectingToDataBase.connectingToDataBase(env);
		this.branchDaoImpl = new LibraryBranchDaoImpl(conn);
		this.bookDaoImpl = new BookDaoImpl(conn);
		this.copiesDaoImpl = new CopiesDaoImpl(conn);
	}

	public void updateBranch(Branch branch) throws UpdateException {
		try {
			branchDaoImpl.update(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a particular branch");
			rollingBack();
			throw new UpdateException("Failed to update branch");
		}
	}
	

	public void setBranchCopies(Branch branch, Book book, int noOfCopies) throws UnknownSQLException {
		try {
			copiesDaoImpl.setCopies(branch, book, noOfCopies);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to set copies for book_copies with BranchId = " + branch.getId() + " and BookId = " + book.getId());
			rollingBack();
			throw new UnknownSQLException("Failed to set Branch Copies", e);
		}
	}
	
	public List<Book> getAllBooks() {
		List<Book> listOfBook = new ArrayList<>();
		try {
			listOfBook = bookDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to give a list of all books in the book table");
		}
		return listOfBook;
	}
	

	public Map<Branch, Map<Book, Integer>> getAllCopies() {
		Map<Branch, Map<Book, Integer>> listOfAllCopies = new HashMap<>();
		try {
			listOfAllCopies = copiesDaoImpl.getAllCopies();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get a list of all copies in the book_copies table");
		}
		return listOfAllCopies;
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
