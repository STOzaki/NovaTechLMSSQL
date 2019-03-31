package com.lms.dao;

import java.sql.SQLException;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Branch;

public interface CopiesDao {
	public abstract int getCopies(Branch branch, Book book) throws SQLException; // if no copies, returns 0
	public abstract void setCopies(Branch branch , Book book, int noOfCopies) throws SQLException; // if int is 0, delete
	Map<Book, Integer> getAllBranchCopies(Branch branch) throws SQLException;
	Map<Branch, Integer> getAllBookCopies(Book book) throws SQLException;
	Map<Branch, Map<Book, Integer>> getAllCopies() throws SQLException;
}
