package com.lms.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public interface BookLoans extends Dao<Loan> {
	public abstract void create(Book book, Borrower borrower, Branch branch, LocalDateTime dateOut, LocalDate dueDate);
}
