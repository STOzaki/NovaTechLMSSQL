package com.lms.dao.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.CopiesDaoImpl;
import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Book;
import com.lms.model.Branch;

import com.lms.service.util.ConnectingToDataBase;

public class CopiesDaoTest {
	private static Connection conn = null;

	private static String table = "tbl_book_copies";
	private static String tableBookId = "bookId";

	private String title = "The Book Title";

	private String branchName = "The Branch Name";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private static final int noOfCopies = 50;
	
	private static BookDaoImpl bookDaoImpl;
	private static LibraryBranchDaoImpl branchDaoImpl;
	private static CopiesDaoImpl copiesDaoImpl;
	
	private Book testBook;
	private Branch testBranch;

	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		bookDaoImpl = new BookDaoImpl(conn);
		branchDaoImpl = new LibraryBranchDaoImpl(conn);
		copiesDaoImpl = new CopiesDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, CriticalSQLException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBook = bookDaoImpl.create(title, null, null);
		testBranch = branchDaoImpl.create(branchName, branchAddress);
		copiesDaoImpl.setCopies(testBranch, testBook, noOfCopies);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		bookDaoImpl.delete(testBook);
		branchDaoImpl.delete(testBranch);
	}
	
	private int mySQLSize() throws SQLException {
		String sql = "SELECT COUNT(" + tableBookId + ") AS size FROM " + table + ";";
		PreparedStatement prepareStatement = conn.prepareStatement(sql);
		ResultSet resultSet = prepareStatement.executeQuery();
		resultSet.next();
		int size = resultSet.getInt("size");
		return size;
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getCopiesTest() throws SQLException {
		int foundNoOfCopies = copiesDaoImpl.getCopies(testBranch, testBook);
		assertEquals(noOfCopies, foundNoOfCopies);
	}
	
	@DisplayName("return 0 if not found")
	@Test
	public void getNonExistingCopiesTest() throws SQLException {
		Branch nonExistingBranch = new Branch(Integer.MAX_VALUE, branchName, branchAddress);
		int foundNoOfCopies = copiesDaoImpl.getCopies(nonExistingBranch, testBook);
		assertEquals(0, foundNoOfCopies);
	}
	
	@DisplayName("Deleting an entry if noOfCopies is 0")
	@Test
	public void setEntryWithNoOfCopiesTest() throws SQLException {
		int previousSize = mySQLSize();
		copiesDaoImpl.setCopies(testBranch, testBook, 0);
		int currentSize = mySQLSize();
		assertEquals(previousSize - 1, currentSize);
	}
	
	@DisplayName("Updating an entry if it exists")
	@Test
	public void setEntryWithNewNoOfCopies() throws SQLException {
		int newNoOfCopies = 100;
		int previousSize = mySQLSize();
		copiesDaoImpl.setCopies(testBranch, testBook, newNoOfCopies);
		int currentSize = mySQLSize();
		int foundNoOfCopies = copiesDaoImpl.getCopies(testBranch, testBook);
		
		assertEquals(previousSize, currentSize);
		assertEquals(newNoOfCopies, foundNoOfCopies);
	}
	
	@Test
	public void getAllBranchCopies() throws SQLException {
		Map<Book, Integer> allBranchCopies = copiesDaoImpl.getAllBranchCopies(testBranch);
		assertTrue(allBranchCopies.containsKey(testBook));
		assertEquals(noOfCopies, allBranchCopies.get(testBook).intValue());
	}
	
	@Test
	public void getAllBookCopiesTest() throws SQLException {
		Map<Branch, Integer> allBookCopies = copiesDaoImpl.getAllBookCopies(testBook);
		assertTrue(allBookCopies.containsKey(testBranch));
		assertEquals(noOfCopies, allBookCopies.get(testBranch).intValue());
	}
	
	@Test
	public void getAllCopiesTest() throws SQLException {
		Map<Branch, Map<Book, Integer>> allCopies = copiesDaoImpl.getAllCopies();
		assertTrue(allCopies.containsKey(testBranch));
		assertTrue(allCopies.get(testBranch).containsKey(testBook));
		assertEquals(noOfCopies, allCopies.get(testBranch).get(testBook).intValue());
	}
}
