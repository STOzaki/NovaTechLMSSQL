package com.lms.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public interface BookLoansDao {
	public abstract Loan create(Book book, Borrower borrower, Branch branch, LocalDateTime dateOut, LocalDate dueDate) throws SQLException;
	public abstract void update(Loan loan) throws SQLException;
	public abstract void delete(Loan loan) throws SQLException;
	public abstract Loan get(Book book, Borrower borrower, Branch branch) throws SQLException;
	public abstract List<Loan> getAll() throws SQLException;
}
