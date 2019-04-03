package com.lms.service;

import java.sql.SQLException;
import java.sql.Connection;

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

public class LibrarianServiceImpl implements LibrarianService {
	private LibraryBranchDaoImpl branchDaoImpl;
	private BookDaoImpl bookDaoImpl;
	private CopiesDaoImpl copiesDaoImpl;
	private static final Logger LOGGER = Logger.getLogger(LibrarianServiceImpl.class.getName());

	public LibrarianServiceImpl(LibraryBranchDaoImpl branchDaoImpl, BookDaoImpl bookDaoImpl,
			CopiesDaoImpl copiesDaoImpl, Connection conn) {
		this.branchDaoImpl = branchDaoImpl;
		this.bookDaoImpl = bookDaoImpl;
		this.copiesDaoImpl = copiesDaoImpl;
	}

	public void updateBranch(Branch branch) {
		try {
			branchDaoImpl.update(branch);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to update a particular branch");
		}
	}
	

	public void setBranchCopies(Branch branch, Book book, int noOfCopies) {
		try {
			copiesDaoImpl.setCopies(branch, book, noOfCopies);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Failed to set copies for book_copies with BranchId = " + branch.getId() + " and BookId = " + book.getId());
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
}
