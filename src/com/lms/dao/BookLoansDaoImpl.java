package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

public class BookLoansDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_book_loans";
	
	public BookLoansDaoImpl(Connection conn) {
		this.conn = conn;
	}

	public Loan create(Book book, Borrower borrower, Branch branch, LocalDateTime dateOut, LocalDate dueDate) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "INSERT INTO " + table + " (bookId, branchId, cardNo, dateOut, dueDate) VALUES (?, ?, ?, ?, ?);";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, book.getId());
		prepareStatement.setInt(2, branch.getId());
		prepareStatement.setInt(3, borrower.getCardNo());
		
		if(dateOut == null) {
			prepareStatement.setNull(4, Types.NULL);
		} else {
			prepareStatement.setDate(4, java.sql.Date.valueOf(dateOut.toLocalDate()));
		}
		
		if(dueDate == null) {
			prepareStatement.setNull(5, Types.NULL);
		} else {
			prepareStatement.setDate(5, java.sql.Date.valueOf(dueDate));
		}
		
		prepareStatement.executeUpdate();
		
		sql = "SELECT * FROM " + table + " ORDER BY bookId DESC LIMIT 1;";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();

		resultSet.next();
		LocalDateTime newDateOut = null;
		if(resultSet.getDate("dateOut") != null) {
			newDateOut = Instant.ofEpochMilli( resultSet.getDate("dateOut").getTime() )
			        .atZone( ZoneId.systemDefault() )
			        .toLocalDateTime();
		}
		
		LocalDate newDueDate = null;
		if(resultSet.getDate("dueDate") != null) {
			newDueDate = Instant.ofEpochMilli( resultSet.getDate("dueDate").getTime() )
			        .atZone( ZoneId.systemDefault() )
			        .toLocalDate();
		}
		
		Loan returnLoan = new Loan(book, borrower, branch, newDateOut, newDueDate);
		return returnLoan;
	}

	public void update(Loan loan) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "UPDATE " + table + " SET dateOut = ?, dueDate = ? WHERE bookId = ? AND branchId = ? AND cardNo = ?;";
		prepareStatement = conn.prepareStatement(sql);
		
		if(loan.getDateOut() == null) {
			prepareStatement.setNull(1, Types.NULL);
		} else {
			prepareStatement.setDate(1, java.sql.Date.valueOf(loan.getDateOut().toLocalDate()));
		}
		
		if(loan.getDueDate() == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setDate(2, java.sql.Date.valueOf(loan.getDueDate()));
		}
		prepareStatement.setInt(3, loan.getBook().getId());
		prepareStatement.setInt(4, loan.getBranch().getId());
		prepareStatement.setInt(5, loan.getBorrower().getCardNo());
		prepareStatement.executeUpdate();
	}

	public void delete(Loan loan) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "DELETE FROM " + table + " WHERE bookId = ? AND branchId = ? AND cardNo = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, loan.getBook().getId());
		prepareStatement.setInt(2, loan.getBranch().getId());
		prepareStatement.setInt(3, loan.getBorrower().getCardNo());
		prepareStatement.executeUpdate();
	}

	public Loan get(Book book, Borrower borrower, Branch branch) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM " + table + " WHERE bookId = ? AND branchId = ? AND cardNo = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, book.getId());
		prepareStatement.setInt(2, branch.getId());
		prepareStatement.setInt(3, borrower.getCardNo());
		resultSet = prepareStatement.executeQuery();
		
		Loan returnLoan = null;
		if(resultSet.next()) {
			BookDaoImpl bookDaoImpl = new BookDaoImpl(conn);
			LibraryBranchDaoImpl branchDaoImpl = new LibraryBranchDaoImpl(conn);
			BorrowerDaoImpl borrowerDaoImpl = new BorrowerDaoImpl(conn);

			Book foundBook = bookDaoImpl.get(resultSet.getInt("bookId"));
			Branch foundBranch = branchDaoImpl.get(resultSet.getInt("branchId"));
			Borrower foundBorrower = borrowerDaoImpl.get(resultSet.getInt("cardNo"));
			
			LocalDateTime foundDateOut = null;
			if(resultSet.getDate("dateOut") != null) {
				foundDateOut = Instant.ofEpochMilli( resultSet.getDate("dateOut").getTime() )
				        .atZone( ZoneId.systemDefault() )
				        .toLocalDateTime();
			}
			
			LocalDate foundDueDate = null;
			if(resultSet.getDate("dueDate") != null) {
				foundDueDate = Instant.ofEpochMilli( resultSet.getDate("dueDate").getTime() )
				        .atZone( ZoneId.systemDefault() )
				        .toLocalDate();
			}
			returnLoan = new Loan(foundBook, foundBorrower, foundBranch, foundDateOut, foundDueDate);
		}
		return returnLoan;
	}
	
	public List<Loan> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<Loan> returnList = new ArrayList<>();
		
		String sql = "SELECT * FROM " + table + ";";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();
		while (resultSet.next()) {
			BookDaoImpl bookDaoImpl = new BookDaoImpl(conn);
			LibraryBranchDaoImpl branchDaoImpl = new LibraryBranchDaoImpl(conn);
			BorrowerDaoImpl borrowerDaoImpl = new BorrowerDaoImpl(conn);

			Book foundBook = bookDaoImpl.get(resultSet.getInt("bookId"));
			Branch foundBranch = branchDaoImpl.get(resultSet.getInt("branchId"));
			Borrower foundBorrower = borrowerDaoImpl.get(resultSet.getInt("cardNo"));
			
			LocalDateTime foundDateOut = null;
			if(resultSet.getDate("dateOut") != null) {
				foundDateOut = Instant.ofEpochMilli( resultSet.getDate("dateOut").getTime() )
				        .atZone( ZoneId.systemDefault() )
				        .toLocalDateTime();
			}
			
			LocalDate foundDueDate = null;
			if(resultSet.getDate("dueDate") != null) {
				foundDueDate = Instant.ofEpochMilli( resultSet.getDate("dueDate").getTime() )
				        .atZone( ZoneId.systemDefault() )
				        .toLocalDate();
			}

			returnList.add(new Loan(foundBook, foundBorrower, foundBranch, foundDateOut, foundDueDate));
		}
		return returnList;
	}

}
