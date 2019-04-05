package com.lms.service.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
import com.lms.customExceptions.UpdateException;
import com.lms.model.Book;
import com.lms.model.Borrower;
import com.lms.model.Branch;
import com.lms.model.Loan;
import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;

public class AdministratorServiceTest {

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private String borrowerName = "The Borrower Name";
	private String borrowerAddress = "650 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";

	// due date is two weeks from now
	private static final LocalDate officialDueDate = LocalDate.now().plusWeeks(2);
	
	private Borrower testBorrower;
	private Book testBook;
	private Loan testLoan;
	private Branch testBranch;
	
	private static AdministratorServiceImpl adminService;
	private static BorrowerServiceImpl borrowerService;

	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		adminService = new AdministratorServiceImpl("test");
		borrowerService = new BorrowerServiceImpl("test");
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, SQLException, CriticalSQLException {
		adminService.closeConnection();
		borrowerService.closeConnection();
	}
	
	@BeforeEach
	public void init() throws SQLException, CriticalSQLException, InsertException {
		testBorrower = adminService.createBorrower(borrowerName, borrowerAddress, borrowerPhone);
		testBook = adminService.createBook(title, null, null);
		testBranch = adminService.createBranch(branchName, branchAddress);
		testLoan = borrowerService.borrowBook(testBorrower, testBook, testBranch, LocalDateTime.now(), officialDueDate);
	}
	
	@AfterEach
	public void tearThis() throws SQLException, DeleteException, RetrieveException {
		adminService.deleteBorrower(testBorrower);
		adminService.deleteBook(testBook);
		adminService.deleteBranch(testBranch);
		borrowerService.returnBook(testBorrower, testBook, testBranch, LocalDate.now());
	}
	
	@DisplayName("Override due date correctly")
	@Test
	public void overrideDueDateForLoanTest() throws SQLException, UpdateException, RetrieveException {
		boolean success = adminService.overrideDueDateForLoan(testBook, testBorrower, testBranch,
				officialDueDate.plusWeeks(1));
		
		List<Loan> listOfLoansWithOneLoan = getListThatMatches(testBook, testBorrower, testBranch);
		
		// make sure that there is only one loan, which there should be!
		assertEquals(1, listOfLoansWithOneLoan.size());
		
		Loan foundLoan = listOfLoansWithOneLoan.get(0);

		assertTrue(success);
		assertEquals(testLoan.getDueDate().plusWeeks(1), foundLoan.getDueDate());
	}
	
	@DisplayName("Override due date fails because there is no such loan")
	@Test
	public void overrideDueDateForNullLoanTest() throws SQLException, UpdateException, RetrieveException {
		Book nonExistingBook = new Book(Integer.MAX_VALUE, "Some Title", null, null);
		boolean success = adminService.overrideDueDateForLoan(nonExistingBook, testBorrower, testBranch,
				officialDueDate.plusWeeks(1));
		
		List<Loan> listOfLoansWithNoLoan = getListThatMatches(nonExistingBook, testBorrower, testBranch);
		
		// The loan does not exist, so there should not be anything in it
		assertEquals(0, listOfLoansWithNoLoan.size());
		
		
		List<Loan> listOfLoansWithOneLoan = getListThatMatches(testBook, testBorrower, testBranch);
		
		// make sure that there is only one loan, which there should be!
		assertEquals(1, listOfLoansWithOneLoan.size());
		
		Loan foundLoan = listOfLoansWithOneLoan.get(0);

		assertFalse(success);
		assertEquals(testLoan.getDueDate(), foundLoan.getDueDate());
	}
	
	private List<Loan> getListThatMatches(Book book, Borrower borrower, Branch branch) throws RetrieveException {
		List<Loan> tempListOfAllLoans = adminService.getAllLoans();
		List<Loan> listOfAllLoansThatMatch = tempListOfAllLoans.parallelStream().filter(l -> l.getBook().equals(book) &&
				l.getBorrower().equals(borrower) && l.getBranch().equals(branch))
			.collect(Collectors.toList());
		return listOfAllLoansThatMatch;
	}
}
