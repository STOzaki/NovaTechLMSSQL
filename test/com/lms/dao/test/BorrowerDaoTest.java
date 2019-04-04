package com.lms.dao.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.lms.customExceptions.CriticalSQLException;
import com.lms.dao.BorrowerDaoImpl;
import com.lms.model.Borrower;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.lms.service.util.ConnectingToDataBase;

public class BorrowerDaoTest {
	private String borrowerName = "Jack Blaze";
	private String borrowerAddress = "601 New Jersey Ave, Washington, DC 20001";
	private String borrowerPhone = "1234567890";
	
	private Borrower testBorrower;
	private static BorrowerDaoImpl borrowerDaoImpl;
	
	private static Connection conn = null;
	private static String table = "tbl_borrower";
	private static String tableId = "cardNo";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		borrowerDaoImpl = new BorrowerDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, CriticalSQLException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBorrower = borrowerDaoImpl.create(borrowerName, borrowerAddress, borrowerPhone);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		borrowerDaoImpl.delete(testBorrower);
	}
	
	private int mySQLSize() throws SQLException {
		String sql = "SELECT COUNT(" + tableId + ") AS size FROM " + table + ";";
		PreparedStatement prepareStatement = conn.prepareStatement(sql);
		ResultSet resultSet = prepareStatement.executeQuery();
		resultSet.next();
		int size = resultSet.getInt("size");
		return size;
	}
	
	@Test
	public void createBorrowerTest() throws SQLException {
		borrowerDaoImpl.delete(testBorrower);
		
		int previousSize = mySQLSize();
		
		testBorrower = borrowerDaoImpl.create(borrowerName, borrowerAddress, borrowerPhone);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testBorrower.getName(), borrowerName);
		assertEquals(testBorrower.getAddress(), borrowerAddress);
		assertEquals(testBorrower.getPhone(), borrowerPhone);
	}
	
	@Test
	public void deleteBorrowerTest() throws SQLException {
		int previousSize = mySQLSize();
		
		borrowerDaoImpl.delete(testBorrower);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(borrowerDaoImpl.get(testBorrower.getCardNo()));
	}

	@DisplayName("Update Correctly")
	@Test
	public void updateBorrowerTest() throws SQLException {
		String newBorrowerName = "New Borrower Name";
		String newBorrowerAddress = "New Address";
		String newBorrowerPhone = "4567891230";

		Borrower newBorrower = new Borrower(testBorrower.getCardNo(), newBorrowerName, newBorrowerAddress, newBorrowerPhone);
		
		borrowerDaoImpl.update(newBorrower);
		
		Borrower updatedborrower = borrowerDaoImpl.get(newBorrower.getCardNo());
		
		assertNotNull(updatedborrower);
		assertEquals(newBorrower, updatedborrower);
	}
	
	@DisplayName("Update even if name is null")
	@Test
	public void updateWithNameNullTest() throws SQLException {
		String newBorrowerAddress = "New Address";
		String newBorrowerPhone = "4567891230";
		Borrower newBorrower = new Borrower(testBorrower.getCardNo(), null, newBorrowerAddress, newBorrowerPhone);
		
		borrowerDaoImpl.update(newBorrower);
		
		Borrower updatedBorrower = borrowerDaoImpl.get(newBorrower.getCardNo());
		
		assertNotNull(updatedBorrower);
		assertEquals(newBorrower, updatedBorrower);
		assertNull(updatedBorrower.getName());
	}
	
	@DisplayName("Update even if address is null")
	@Test
	public void updateWithAddressNullTest() throws SQLException {
		String newBorrowerName = "New Borrower Name";
		String newBorrowerPhone = "4567891230";
		Borrower newBorrower = new Borrower(testBorrower.getCardNo(), newBorrowerName, null, newBorrowerPhone);
		
		borrowerDaoImpl.update(newBorrower);
		
		Borrower updatedBorrower = borrowerDaoImpl.get(newBorrower.getCardNo());
		
		assertNotNull(updatedBorrower);
		assertEquals(newBorrower, updatedBorrower);
		assertNull(updatedBorrower.getAddress());
	}
	
	@DisplayName("Update even if phone is null")
	@Test
	public void updateWithPhoneNullTest() throws SQLException {
		String newBorrowerName = "New Borrower Name";
		String newBorrowerAddress = "New Address";
		Borrower newBorrower = new Borrower(testBorrower.getCardNo(), newBorrowerName, newBorrowerAddress, null);
		
		borrowerDaoImpl.update(newBorrower);
		
		Borrower updatedBorrower = borrowerDaoImpl.get(newBorrower.getCardNo());
		
		assertNotNull(updatedBorrower);
		assertEquals(newBorrower, updatedBorrower);
		assertNull(updatedBorrower.getPhone());
	}
	
	@DisplayName("Get Borrower correctly")
	@Test
	public void getBorrowerTest() throws SQLException {
		Borrower foundBorrower = borrowerDaoImpl.get(testBorrower.getCardNo());
		assertNotNull(foundBorrower);
		assertEquals(foundBorrower, testBorrower);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundBorrowerTest() throws SQLException {
		Borrower foundBorrower = borrowerDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundBorrower);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Borrower> listOfBorrowers = borrowerDaoImpl.getAll();
		int borrowerSize = mySQLSize();
		assertEquals(listOfBorrowers.size(), borrowerSize);
	}
}
