package com.lms.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public interface BookLoans {
	public abstract Loan create(Book book, Borrower borrower, Branch branch, LocalDateTime dateOut, LocalDate dueDate) throws SQLException;
	public abstract void update(Book book) throws SQLException;
	public abstract void delete(Book book) throws SQLException;
	public abstract Book get(Book book, Borrower borrower, Branch branch) throws SQLException;
	public abstract List<Book> getAll() throws SQLException;
}
