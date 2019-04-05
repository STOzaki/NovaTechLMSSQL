package com.lms.service;

import java.sql.SQLException;
import java.sql.Connection;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.customExceptions.UpdateException;
import com.lms.customExceptions.RetrieveException;
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
	
	public void closeConnection() {
		ConnectingToDataBase.closingConnection(conn);
	}

	public void updateBranch(Branch branch) throws UpdateException, CriticalSQLException {
		try {
			branchDaoImpl.update(branch);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a particular branch", e);
			rollingBack();
			throw new UpdateException("Failed to update branch", e);
		}
	}
	

	public void setBranchCopies(Branch branch, Book book, int noOfCopies) throws UnknownSQLException, CriticalSQLException {
		try {
			copiesDaoImpl.setCopies(branch, book, noOfCopies);
			conn.commit();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to set copies for book_copies with BranchId = " + branch.getId() + " and BookId = " + book.getId(), e);
			rollingBack();
			throw new UnknownSQLException("Failed to set Branch Copies", e);
		}
	}
	
	public List<Book> getAllBooks() throws RetrieveException {
		List<Book> listOfBook = new ArrayList<>();
		try {
			listOfBook = bookDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to give a list of all books in the book table", e);
			throw new RetrieveException("Failed to get a list of all books", e);
		}
		return listOfBook;
	}
	

	public Map<Branch, Map<Book, Integer>> getAllCopies() throws RetrieveException {
		Map<Branch, Map<Book, Integer>> listOfAllCopies = new HashMap<>();
		try {
			listOfAllCopies = copiesDaoImpl.getAllCopies();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to get a list of all copies in the book_copies table", e);
			throw new RetrieveException("Failed to get a list of all copies in the book_copies table", e);
		}
		return listOfAllCopies;
	}
	
	public List<Branch> getAllBranches() throws RetrieveException {
		List<Branch> listOfBranches = new ArrayList<>();
		try {
			listOfBranches = branchDaoImpl.getAll();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to give a list of all branches in the branch table", e);
			throw new RetrieveException("Failed to get a list of all books", e);
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
