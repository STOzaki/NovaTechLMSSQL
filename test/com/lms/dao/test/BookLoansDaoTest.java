package com.lms.dao.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;

import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;

import com.lms.service.util.ConnectingToDataBase;

public class BookLoansDaoTest {

	private static Connection conn = null;

	private static String table = "tbl_book_loans";
	private static String tableBookId = "bookId";

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private String borrowerName = "The Borrower Name";
	private String borrowerAddress = "650 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";
	
	// at 00:00 because the time gets converted to date in sql
	private LocalDateTime dateOut = LocalDate.now().atTime(00,00);
	private LocalDate dueDate = LocalDate.now().plusWeeks(1);
	
	private static BookDaoImpl bookDaoImpl;
	private static LibraryBranchDaoImpl branchDaoImpl;
	private static BorrowerDaoImpl borrowerDaoImpl;
	private static BookLoansDaoImpl loansDaoImpl;
	
	private Book testBook;
	private Branch testBranch;
	private Borrower testBorrower;
	private Loan testLoan;
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		bookDaoImpl = new BookDaoImpl(conn);
		branchDaoImpl = new LibraryBranchDaoImpl(conn);
		borrowerDaoImpl = new BorrowerDaoImpl(conn);
		loansDaoImpl = new BookLoansDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBook = bookDaoImpl.create(title, null, null);
		testBranch = branchDaoImpl.create(branchName, branchAddress);
		testBorrower = borrowerDaoImpl.create(borrowerName, borrowerAddress, borrowerPhone);
		testLoan = loansDaoImpl.create(testBook, testBorrower, testBranch, dateOut, dueDate);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		bookDaoImpl.delete(testBook);
		branchDaoImpl.delete(testBranch);
		borrowerDaoImpl.delete(testBorrower);
	}
	
	private int mySQLSize() throws SQLException {
		String sql = "SELECT COUNT(" + tableBookId + ") AS size FROM " + table + ";";
		PreparedStatement prepareStatement = conn.prepareStatement(sql);
		ResultSet resultSet = prepareStatement.executeQuery();
		resultSet.next();
		int size = resultSet.getInt("size");
		return size;
	}
	
	@Test
	public void createLoanTest() throws SQLException {
		loansDaoImpl.delete(testLoan);
		
		int previousSize = mySQLSize();
		
		testLoan = loansDaoImpl.create(testBook, testBorrower, testBranch, dateOut, dueDate);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testBook, testLoan.getBook());
		assertEquals(testBorrower, testLoan.getBorrower());
		assertEquals(testBranch, testLoan.getBranch());
		assertEquals(dateOut, testLoan.getDateOut());
		assertEquals(dueDate, testLoan.getDueDate());
	}
	
	@Test
	public void deleteLoanTest() throws SQLException {
		int previousSize = mySQLSize();
		
		loansDaoImpl.delete(testLoan);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(loansDaoImpl.get(testLoan.getBook(), testLoan.getBorrower(), testLoan.getBranch()));
	}

	@DisplayName("Update Correctly")
	@Test
	public void updateLoansTest() throws SQLException {
		LocalDateTime newDateOut = LocalDate.now().plusDays(5).atTime(00,00);
		LocalDate newDueDate = LocalDate.now().plusDays(5).plusWeeks(1);

		Loan newLoan = new Loan(testLoan.getBook(), testLoan.getBorrower(), testLoan.getBranch(), newDateOut, newDueDate);
		
		loansDaoImpl.update(newLoan);
		
		Loan updatedloans = loansDaoImpl.get(newLoan.getBook(), newLoan.getBorrower(), newLoan.getBranch());
		
		assertNotNull(updatedloans);
		assertEquals(newLoan, updatedloans);
	}
	
	@DisplayName("Update even if dateOut is null")
	@Test
	public void updateWithDateOutNullTest() throws SQLException {
		LocalDate newDueDate = LocalDate.now().plusDays(5).plusWeeks(1);

		Loan newLoan = new Loan(testLoan.getBook(), testLoan.getBorrower(), testLoan.getBranch(), null, newDueDate);
		
		loansDaoImpl.update(newLoan);
		
		Loan updatedLoan = loansDaoImpl.get(newLoan.getBook(), newLoan.getBorrower(), newLoan.getBranch());
		
		assertNotNull(updatedLoan);
		assertEquals(newLoan, updatedLoan);
		assertNull(updatedLoan.getDateOut());
	}
	
	@DisplayName("Update even if dueDate is null")
	@Test
	public void updateWithDueDateNullTest() throws SQLException {
		LocalDateTime newDateOut = LocalDate.now().plusDays(5).atTime(00,00);

		Loan newLoan = new Loan(testLoan.getBook(), testLoan.getBorrower(), testLoan.getBranch(), newDateOut, null);
		
		loansDaoImpl.update(newLoan);
		
		Loan updatedLoan = loansDaoImpl.get(newLoan.getBook(), newLoan.getBorrower(), newLoan.getBranch());
		
		assertNotNull(updatedLoan);
		assertEquals(newLoan, updatedLoan);
		assertNull(updatedLoan.getDueDate());
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getLoanTest() throws SQLException {
		Loan foundLoan = loansDaoImpl.get(testLoan.getBook(), testLoan.getBorrower(), testLoan.getBranch());
		assertNotNull(foundLoan);
		assertEquals(foundLoan, testLoan);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundBookTest() throws SQLException {
		Book foundBook = bookDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundBook);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Loan> listOfLoans = loansDaoImpl.getAll();
		int loanSize = mySQLSize();
		assertEquals(listOfLoans.size(), loanSize);
	}

}
