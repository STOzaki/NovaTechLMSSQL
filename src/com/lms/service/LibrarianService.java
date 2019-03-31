package com.lms.service;

import java.util.List;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Branch;

/**
 * A service interface to ease the creation of a UI for librarians.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface LibrarianService extends Service {
	/**
	 * Update the database's record of the given branch to match the object's state.
	 * @param branch the branch to update in the database.
	 */
	public abstract void updateBranch(Branch branch);

	/**
	 * Set the number of copies of the given book that the given branch owns.
	 *
	 * @param branch     the branch in question
	 * @param book       the book in question
	 * @param noOfCopies the number of copies of that book at that branch
	 */
	public abstract void setBranchCopies(Branch branch, Book book, int noOfCopies);
	/**
	 * Get all books in the database.
	 * @return all books in the database
	 */
	public abstract List<Book> getAllBooks();

	/**
	 * Get all counts of copies that branches have, as a mapping from branches to
	 * book-to-copy mappings.
	 *
	 * <p>TODO: Would this be more useful with Branch and Book switched in its return
	 * type?
	 *
	 * @return the collection of all copy counts in the database
	 */
	public abstract Map<Branch, Map<Book, Integer>> getAllCopies();
}
