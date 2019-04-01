package com.lms.dao.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.lms.dao.PublisherDaoImpl;
import com.lms.model.Publisher;


public class PublisherDaoTest {
	private String publisherName = "The Publisher";
	private String publisherAddress = "601 New Jersey Ave, Washington, DC 20001";
	private String publisherPhone = "1234567890";
	private static Connection conn = null;
	private static PublisherDaoImpl publisherDaoImpl;
	private Publisher testPublisher;
	private static BufferedReader br;
	private static String table = "tbl_publisher";
	private static String tableId = "publisherId";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException {
		br = new BufferedReader(new FileReader(".config"));
		List<String> authentication = new ArrayList<>();
		String nextLine = "";
		while((nextLine = br.readLine()) != null) {
			authentication.add(nextLine);
		}
		conn = (Connection) DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/libraryTest?useSSL=false&serverTimezone=UTC", authentication.get(0), authentication.get(1));
		publisherDaoImpl = new PublisherDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		br.close();
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testPublisher = publisherDaoImpl.create(publisherName, publisherAddress, publisherPhone);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		publisherDaoImpl.delete(testPublisher);
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
	public void createPublisherTest() throws SQLException {
		publisherDaoImpl.delete(testPublisher);
		
		int previousSize = mySQLSize();
		
		testPublisher = publisherDaoImpl.create(publisherName, publisherAddress, publisherPhone);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testPublisher.getName(), publisherName);
		assertEquals(testPublisher.getAddress(), publisherAddress);
		assertEquals(testPublisher.getPhone(), publisherPhone);
	}
	
	@Test
	public void deletePublisherTest() throws SQLException {
		int previousSize = mySQLSize();
		
		publisherDaoImpl.delete(testPublisher);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(publisherDaoImpl.get(testPublisher.getId()));
	}

	@DisplayName("Update Correctly")
	@Test
	public void updatePublisherTest() throws SQLException {
		Publisher newPublisher = new Publisher(testPublisher.getId(), "Publisher Person", "123 new address in VA", "9876543210");
		
		publisherDaoImpl.update(newPublisher);
		
		Publisher updatedPublisher = publisherDaoImpl.get(newPublisher.getId());
		
		assertNotNull(updatedPublisher);
		assertEquals(newPublisher, updatedPublisher);
	}
	
	@DisplayName("Update even if address is null")
	@Test
	public void updateWithAddressNullTest() throws SQLException {
		Publisher newPublisher = new Publisher(testPublisher.getId(), "Publisher Person", null, "9876543210");
		
		publisherDaoImpl.update(newPublisher);
		
		Publisher updatedPublisher = publisherDaoImpl.get(newPublisher.getId());
		
		assertNotNull(updatedPublisher);
		assertEquals(newPublisher, updatedPublisher);
		assertNull(updatedPublisher.getAddress());
	}

	@DisplayName("Update even if phone is null")
	@Test
	public void updateWithPhoneNullTest() throws SQLException {
		Publisher newPublisher = new Publisher(testPublisher.getId(), "Publisher Person", "123 new address in VA", null);
		
		publisherDaoImpl.update(newPublisher);
		
		Publisher updatedPublisher = publisherDaoImpl.get(newPublisher.getId());
		
		assertNotNull(updatedPublisher);
		assertEquals(newPublisher, updatedPublisher);
		assertNull(updatedPublisher.getPhone());
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getPublisherTest() throws SQLException {
		Publisher foundPublisher = publisherDaoImpl.get(testPublisher.getId());
		assertNotNull(foundPublisher);
		assertEquals(foundPublisher, testPublisher);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundPublisherTest() throws SQLException {
		Publisher foundPublisher = publisherDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundPublisher);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Publisher> listOfPublishers = publisherDaoImpl.getAll();
		int publisherSize = mySQLSize();
		assertEquals(listOfPublishers.size(), publisherSize);
	}
}
