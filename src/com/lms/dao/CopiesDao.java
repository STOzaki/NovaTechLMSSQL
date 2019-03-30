package com.lms.dao;

import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Branch;

public interface CopiesDao {
	public abstract int getCopies(Branch branch, Book book); // if no copies, returns 0
	public abstract void setCopies(Branch branch , Book book, int noOfCopies); // if int is 0, delete
	Map<Book, Integer> getAllBranchCopies(Branch branch);
	Map<Branch, Integer> getAllBookCopies(Book book);
	Map<Branch, Map<Book, Integer>> getAllCopies();
}
