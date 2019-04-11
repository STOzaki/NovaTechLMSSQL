package com.lms.service.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

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
import com.lms.customExceptions.UpdateException;
import com.lms.model.Book;
import com.lms.model.Branch;
import com.lms.service.AdministratorServiceImpl;
import com.lms.service.BorrowerServiceImpl;
import com.lms.service.LibrarianServiceImpl;

public class LibrarianServiceTest {

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";

	private Book testBook;
	private Branch testBranch;

	private static int noOfCopies = 50;

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
		testBook = adminService.createBook(title, null, null);
		testBranch = adminService.createBranch(branchName, branchAddress);
		// due date is two weeks from now
		libService.setBranchCopies(testBranch, testBook, noOfCopies);
	}
	
	@AfterEach
	public void tearThis() throws SQLException, DeleteException, UnknownSQLException, RetrieveException, CriticalSQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		adminService.deleteBook(testBook);
		adminService.deleteBranch(testBranch);
		libService.setBranchCopies(testBranch, testBook, 0);
	}
	
	@DisplayName("throws null pointer exception if null is passed as a parameter for update branch")
	@Test
	public void updateBranchTest() throws UpdateException, CriticalSQLException {
		Branch nullBranch = null;
		assertThrows(NullPointerException.class, () -> {libService.updateBranch(nullBranch);}, "Excpeting to throw null pointer exception");
	}
	
	@DisplayName("throws null pointer exception if branch is null for set copies")
	@Test
	public void setBranchCopiesBranchNullTest() throws UpdateException, CriticalSQLException {
		Branch nullBranch = null;
		assertThrows(NullPointerException.class, () -> {libService.setBranchCopies(nullBranch, testBook, noOfCopies);}, "Excpeting to throw null pointer exception");
	}
	
	@DisplayName("throws null pointer exception if book is null for set copies")
	@Test
	public void setBranchCopiesBookNullTest() throws UpdateException, CriticalSQLException {
		Book nullBook = null;
		assertThrows(NullPointerException.class, () -> {libService.setBranchCopies(testBranch, nullBook, noOfCopies);}, "Excpeting to throw null pointer exception");
	}
	
	@DisplayName("Adds noOfCopies to the book copies table if it doesnt already exist")
	@Test
	public void setBranchCopiesNonExistingTest() throws CriticalSQLException, RetrieveException, UnknownSQLException {
		Map<Branch, Map<Book, Integer>> previousListOfCopies = libService.getAllCopies();
		assertTrue(previousListOfCopies.containsKey(testBranch));
		
		int customNoOfCopies = 99;
		libService.setBranchCopies(testBranch, testBook, customNoOfCopies);
		Map<Branch, Map<Book, Integer>> currentListOfCopies = libService.getAllCopies();
		assertTrue(currentListOfCopies.containsKey(testBranch));
		
		assertEquals(customNoOfCopies, currentListOfCopies.get(testBranch).get(testBook).intValue());
	}
}
