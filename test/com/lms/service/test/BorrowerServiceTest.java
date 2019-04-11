package com.lms.service.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.lms.customExceptions.RetrieveException;
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
		libService.setBranchCopies(testBranch, testBook, noOfCopies);
		// due date is two weeks from now
		testLoan = borrowerService.borrowBook(testBorrower, testBook, testBranch, LocalDateTime.now(),
				LocalDate.now().plusWeeks(2));
	}
	
	@AfterEach
	public void tearThis() throws SQLException, DeleteException, UnknownSQLException, RetrieveException, CriticalSQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now());
		libService.setBranchCopies(testBranch, testBook, 0);
		adminService.deleteBorrower(testBorrower);
		adminService.deleteBook(testBook);
		adminService.deleteBranch(testBranch);
	}

	@DisplayName("Can return a book because not over the due date and no. of copies goes back up")
	@Test
	public void returnBookTest() throws DeleteException, RetrieveException, CriticalSQLException, InsertException {
		int noOfCopiesBeforeReturingABook = borrowerService.getAllBranchCopies(testBranch).get(testBook);
		// returning 1 week before it is due
		boolean result = borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now().plusWeeks(1));
		assertTrue(result);
		int noOfCopiesAfterReturingABook = borrowerService.getAllBranchCopies(testBranch).get(testBook);
		
		int newNoOfCopies = borrowerService.getAllBranchCopies(testBranch).get(testBook);
		assertEquals(noOfCopies, newNoOfCopies);
		assertEquals(noOfCopiesBeforeReturingABook + 1, noOfCopiesAfterReturingABook);
		
		borrowerService.borrowBook(testBorrower, testBook, testBranch, LocalDateTime.now(), LocalDate.now().plusWeeks(2));
	}
	
	@DisplayName("Cannot return book if it cannot find that loan")
	@Test
	public void returnNullBookTest() throws DeleteException, RetrieveException, CriticalSQLException, InsertException {
		Book fakeBook = new Book(Integer.MAX_VALUE, "Some Title", null, null);
		boolean result = borrowerService.returnBook(testBorrower, fakeBook, testBranch, LocalDate.now().plusWeeks(1));
		assertFalse(result);
	}
	
	@DisplayName("Cannot return book if due date has already passed")
	@Test
	public void returnBookWithDueDatePassedTest() throws DeleteException, RetrieveException, CriticalSQLException, InsertException {
		// 1 week after book is due
		boolean result = borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now().plusWeeks(3));
		assertFalse(result);
	}
	
	@DisplayName("borrow returns null if there are no copies of that book")
	@Test
	public void borrowBookNullTest() throws InsertException, CriticalSQLException, DeleteException {
		Book newBook = adminService.createBook(title, null, null);
		Loan newLoan = borrowerService.borrowBook(testBorrower, newBook, testBranch, LocalDateTime.now(), LocalDate.now().plusWeeks(2));
		
		assertNull(newLoan);
		
		adminService.deleteBook(newBook);
	}
	
	@DisplayName("borrow a book and no. of copies goes down")
	@Test
	public void borrowBookAndNoOfCopiesDown() throws DeleteException, RetrieveException, CriticalSQLException, InsertException {
		// returning 1 week before it is due
		borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now().plusWeeks(1));
		int noOfCopiesBeforeBorrowing = borrowerService.getAllBranchCopies(testBranch).get(testBook);
		
		borrowerService.borrowBook(testBorrower, testBook, testBranch, LocalDateTime.now(), LocalDate.now().plusWeeks(2));
		int noOfCopiesAfterBorrowing = borrowerService.getAllBranchCopies(testBranch).get(testBook);
		
		assertEquals(noOfCopiesBeforeBorrowing - 1, noOfCopiesAfterBorrowing);
	}
	
	@Test
	public void getAllBranchesWithLoanTest() throws RetrieveException {
		List<Branch> listOfBranchesWithLoans = borrowerService.getAllBranchesWithLoan(testBorrower);
		assertTrue(listOfBranchesWithLoans.contains(testBranch));
		assertEquals(1, listOfBranchesWithLoans.size());
	}
	
	@Test
	public void getAllBorrowedBooksTest() throws RetrieveException {
		List<Loan> listOfAllBorrowed = borrowerService.getAllBorrowedBooks(testBorrower);
		assertTrue(listOfAllBorrowed.contains(testLoan));
		assertEquals(1, listOfAllBorrowed.size());
	}
}
