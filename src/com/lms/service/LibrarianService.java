package com.lms.service;

import java.util.List;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Branch;

public interface LibrarianService extends Service {
	public abstract void updateBranch(Branch branch);
	public abstract void setBranchCopies(Branch branch, Book book, int noOfCopies);
	public abstract List<Book> getAllBooks();
	public abstract Map<Branch, Map<Book, Integer>> getAllCopies(); // could be Map<Book, Map<BranchBook, Integer>> // This needs to be merged with getAllBooks();
}
