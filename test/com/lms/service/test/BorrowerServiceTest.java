package com.lms.service.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lms.customExceptions.DeleteException;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.BorrowerServiceImpl;

public class BorrowerServiceTest {
	
	private static Connection conn = null;
	private static BufferedReader br;

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private String borrowerName = "The Borrower Name";
	private String borrowerAddress = "650 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";
	
	private static int noOfCopies = 50;
	
	private static BorrowerDaoImpl borrowerDaoImpl;
	private static BookLoansDaoImpl loanDaoImpl;
	private static CopiesDaoImpl copiesDaoImpl;
	private static BookDaoImpl bookDaoImpl;
	private static LibraryBranchDaoImpl branchDaoImpl;
	
	private Borrower testBorrower;
	private Book testBook;
	private Loan testLoan;
	private Branch testBranch;
	
	private BorrowerServiceImpl borrowerService;
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException {
		br = new BufferedReader(new FileReader(".config"));
		List<String> authentication = new ArrayList<>();
		String nextLine = "";
		while((nextLine = br.readLine()) != null) {
			authentication.add(nextLine);
		}
		conn = (Connection) DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/libraryTest?useSSL=false&serverTimezone=UTC",
				authentication.get(0), authentication.get(1));
		
		// simulate the same as the menu
		conn.setAutoCommit(false);
		
		borrowerDaoImpl = new BorrowerDaoImpl(conn);
		loanDaoImpl = new BookLoansDaoImpl(conn);
		copiesDaoImpl = new CopiesDaoImpl(conn);
		bookDaoImpl = new BookDaoImpl(conn);
		branchDaoImpl = new LibraryBranchDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, SQLException {
		br.close();
		conn.close();
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBorrower = borrowerDaoImpl.create(borrowerName, borrowerAddress, borrowerPhone);
		testBook = bookDaoImpl.create(title, null, null);
		testBranch = branchDaoImpl.create(branchName, branchAddress);
		// due date is two weeks from now
		testLoan = loanDaoImpl.create(testBook, testBorrower, testBranch, LocalDateTime.now(), LocalDate.now().plusWeeks(2));
		copiesDaoImpl.setCopies(testBranch, testBook, noOfCopies);
		borrowerService = new BorrowerServiceImpl(borrowerDaoImpl, loanDaoImpl, copiesDaoImpl, branchDaoImpl, conn);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		borrowerDaoImpl.delete(testBorrower);
		bookDaoImpl.delete(testBook);
		branchDaoImpl.delete(testBranch);
		loanDaoImpl.delete(testLoan);
		copiesDaoImpl.setCopies(testBranch, testBook, 0);
	}

	@DisplayName("Can return a book because not over the due date")
	@Test
	public void returnBookTest() throws DeleteException {
		// returning 1 week before it is due
		boolean result = borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now().plusWeeks(1));
		assertTrue(result);
	}
	
	@DisplayName("Cannot return book if it cannot find that loan")
	@Test
	public void returnNullBookTest() throws DeleteException {
		Book fakeBook = new Book(Integer.MAX_VALUE, "Some Title", null, null);
		boolean result = borrowerService.returnBook(testBorrower, fakeBook, testBranch, LocalDate.now().plusWeeks(1));
		assertFalse(result);
	}
	
	@DisplayName("Cannot return book if due date has already passed")
	@Test
	public void returnBookWithDueDatePassedTest() throws DeleteException {
		// 1 week after book is due
		boolean result = borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now().plusWeeks(3));
		assertFalse(result);
	}
	
	@Test
	public void getAllBranchesWithLoanTest() {
		List<Branch> listOfBranchesWithLoans = borrowerService.getAllBranchesWithLoan(testBorrower);
		assertTrue(listOfBranchesWithLoans.contains(testBranch));
		assertEquals(1, listOfBranchesWithLoans.size());
	}
	
	@Test
	public void getAllBorrowedBooksTest() {
		List<Loan> listOfAllBorrowed = borrowerService.getAllBorrowedBooks(testBorrower);
		assertTrue(listOfAllBorrowed.contains(testLoan));
		assertEquals(1, listOfAllBorrowed.size());
	}
}
