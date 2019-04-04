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

import com.lms.dao.LibraryBranchDaoImpl;
import com.lms.model.Branch;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.lms.service.util.ConnectingToDataBase;

public class LibraryBranchDaoTest {
	private String branchName = "The Branch";
	private String branchAddress = "601 New Jersey Ave, Washington, DC 20001";
	
	private static LibraryBranchDaoImpl branchDaoImpl;
	private Branch testBranch;
	
	private static Connection conn = null;

	private static String table = "tbl_library_branch";
	private static String tableId = "branchId";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		branchDaoImpl = new LibraryBranchDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testBranch = branchDaoImpl.create(branchName, branchAddress);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		branchDaoImpl.delete(testBranch);
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
	public void createBranchTest() throws SQLException {
		branchDaoImpl.delete(testBranch);
		
		int previousSize = mySQLSize();
		
		testBranch = branchDaoImpl.create(branchName, branchAddress);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testBranch.getName(), branchName);
		assertEquals(testBranch.getAddress(), branchAddress);
	}
	
	@Test
	public void deleteBranchTest() throws SQLException {
		int previousSize = mySQLSize();
		
		branchDaoImpl.delete(testBranch);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(branchDaoImpl.get(testBranch.getId()));
	}

	@DisplayName("Update Branch Correctly")
	@Test
	public void updateBranchTest() throws SQLException {
		String newBranchName = "Branch Person";
		String newBranchAddress = "123 new address in VA";
		Branch newBranch = new Branch(testBranch.getId(), newBranchName, newBranchAddress);
		
		branchDaoImpl.update(newBranch);
		
		Branch updatedBranch = branchDaoImpl.get(newBranch.getId());
		
		assertNotNull(updatedBranch);
		assertEquals(newBranch, updatedBranch);
	}

	@DisplayName("Update even if name is null")
	@Test
	public void updateWithNameNullTest() throws SQLException {
		String newBranchAddress = "123 new address in VA";
		Branch newBranch = new Branch(testBranch.getId(), null, newBranchAddress);
		
		branchDaoImpl.update(newBranch);
		
		Branch updatedBranch = branchDaoImpl.get(newBranch.getId());
		
		assertNotNull(updatedBranch);
		assertEquals(newBranch, updatedBranch);
		assertNull(updatedBranch.getName());
	}
	
	@DisplayName("Update even if address is null")
	@Test
	public void updateWithAddressNullTest() throws SQLException {
		String newBranchName = "Branch Person";
		Branch newBranch = new Branch(testBranch.getId(), newBranchName, null);
		
		branchDaoImpl.update(newBranch);
		
		Branch updatedBranch = branchDaoImpl.get(newBranch.getId());
		
		assertNotNull(updatedBranch);
		assertEquals(newBranch, updatedBranch);
		assertNull(updatedBranch.getAddress());
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getBranchTest() throws SQLException {
		Branch foundBranch = branchDaoImpl.get(testBranch.getId());
		assertNotNull(foundBranch);
		assertEquals(foundBranch, testBranch);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundBranchTest() throws SQLException {
		Branch foundBranch = branchDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundBranch);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Branch> listOfBranchs = branchDaoImpl.getAll();
		int branchSize = mySQLSize();
		assertEquals(listOfBranchs.size(), branchSize);
	}

}
