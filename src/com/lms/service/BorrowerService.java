package com.lms.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public interface BorrowerService extends Service {
	public abstract Loan borrowBook(Borrower borrower, Book book , Branch branch, LocalDateTime dateOut, LocalDate dueDate); // create loan entry
	public abstract Map<Book, Integer> getAllBranchCopies(Branch branch);

	public abstract boolean returnBook(Borrower borrower, Book book , Branch branch); // this will be a custom method that deletes the Loan entry if the book is not overdue. Else does not delete the record and returns a false.
	public abstract List<Branch> getAllBranchesWithLoan(Borrower borrower); // this will be a custom method that gets all branches that the borrower borrowed a book from.
	public abstract List<Book> getAllBorrowedBooks(Borrower borrower); // this will be a custom method that gets all books that the borrower borrowed.

}
