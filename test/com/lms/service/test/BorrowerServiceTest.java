package com.lms.service.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
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

import com.lms.customExceptions.CriticalSQLException;
import com.lms.customExceptions.DeleteException;
import com.lms.customExceptions.InsertException;
import com.lms.customExceptions.UnknownSQLException;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;
import com.lms.service.LibrarianServiceImpl;

public class BorrowerServiceTest {

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private String borrowerName = "The Borrower Name";
	private String borrowerAddress = "650 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";
	
	private static int noOfCopies = 50;

	private Borrower testBorrower;
	private Book testBook;
	private Loan testLoan;
	private Branch testBranch;
	
	private static BorrowerServiceImpl borrowerService;
	private static AdministratorServiceImpl adminService;
	private static LibrarianServiceImpl libService;
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		borrowerService = new BorrowerServiceImpl("test");
		adminService = new AdministratorServiceImpl("test");
		libService = new LibrarianServiceImpl("test");
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, SQLException, CriticalSQLException {
		borrowerService.closeConnection();
		adminService.closeConnection();
		libService.closeConnection();
	}
	
	@BeforeEach
	public void init() throws SQLException, CriticalSQLException, InsertException, UnknownSQLException {
		testBorrower = adminService.createBorrower(borrowerName, borrowerAddress, borrowerPhone);
		testBook = adminService.createBook(title, null, null);
		testBranch = adminService.createBranch(branchName, branchAddress);
		// due date is two weeks from now
		testLoan = borrowerService.borrowBook(testBorrower, testBook, testBranch, LocalDateTime.now(), LocalDate.now().plusWeeks(2));
		libService.setBranchCopies(testBranch, testBook, noOfCopies);
	}
	
	@AfterEach
	public void tearThis() throws SQLException, DeleteException, UnknownSQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		adminService.deleteBorrower(testBorrower);
		adminService.deleteBook(testBook);
		adminService.deleteBranch(testBranch);
		borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now());
		libService.setBranchCopies(testBranch, testBook, 0);
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
