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

import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.BookLoansDaoImpl;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.dao.PublisherDaoImpl;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.AdministratorServiceImpl;

public class AdministratorServiceTest {

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private String borrowerName = "The Borrower Name";
	private String borrowerAddress = "650 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";
	
	private static final LocalDate officialDueDate = LocalDate.now().plusWeeks(2);
	
	private static Connection conn = null;
	private static BufferedReader br;

	private static BookDaoImpl bookDaoImpl;
	private static AuthorDaoImpl authorDaoImpl;
	private static PublisherDaoImpl publisherDaoImpl;
	private static LibraryBranchDaoImpl branchDaoImpl;
	private static BorrowerDaoImpl borrowerDaoImpl;
	private static BookLoansDaoImpl loanDaoImpl;
	
	private Borrower testBorrower;
	private Book testBook;
	private Loan testLoan;
	private Branch testBranch;
	
	private AdministratorServiceImpl adminService;

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
		bookDaoImpl = new BookDaoImpl(conn);
		branchDaoImpl = new LibraryBranchDaoImpl(conn);
		authorDaoImpl = new AuthorDaoImpl(conn);
		publisherDaoImpl = new PublisherDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		br.close();
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBorrower = borrowerDaoImpl.create(borrowerName, borrowerAddress, borrowerPhone);
		testBook = bookDaoImpl.create(title, null, null);
		testBranch = branchDaoImpl.create(branchName, branchAddress);
		// due date is two weeks from now
		testLoan = loanDaoImpl.create(testBook, testBorrower, testBranch, LocalDateTime.now(), officialDueDate);
		adminService = new AdministratorServiceImpl(bookDaoImpl, authorDaoImpl, publisherDaoImpl,
				branchDaoImpl, borrowerDaoImpl, loanDaoImpl, conn);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		borrowerDaoImpl.delete(testBorrower);
		bookDaoImpl.delete(testBook);
		branchDaoImpl.delete(testBranch);
		loanDaoImpl.delete(testLoan);
	}
	
	@DisplayName("Override due date correctly")
	@Test
	public void overrideDueDateForLoanTest() throws SQLException {
		boolean success = adminService.overrideDueDateForLoan(testBook, testBorrower, testBranch,
				officialDueDate.plusWeeks(1));
		Loan foundLoan = loanDaoImpl.get(testBook, testBorrower, testBranch);

		assertTrue(success);
		assertEquals(testLoan.getDueDate().plusWeeks(1), foundLoan.getDueDate());
	}
	
	@DisplayName("Override due date fails because there is no such loan")
	@Test
	public void overrideDueDateForNullLoanTest() throws SQLException {
		Book nonExistingBook = new Book(Integer.MAX_VALUE, "Some Title", null, null);
		boolean success = adminService.overrideDueDateForLoan(nonExistingBook, testBorrower, testBranch,
				officialDueDate.plusWeeks(1));
		Loan foundLoan = loanDaoImpl.get(testBook, testBorrower, testBranch);

		assertFalse(success);
		assertEquals(testLoan.getDueDate(), foundLoan.getDueDate());
	}
}
