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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lms.dao.AuthorDaoImpl;
import com.lms.model.Author;

import com.lms.service.util.ConnectingToDataBase;

public class AuthorDaoTest {
	private String authorName = "Robert Jr.";

	private static Connection conn = null;

	private static AuthorDaoImpl authorDaoImpl;
	private Author testAuthor;
	private static String table = "tbl_author";
	private static String tableId = "authorId";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		authorDaoImpl = new AuthorDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testAuthor = authorDaoImpl.create(authorName);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		// WARNING maybe something that doesn't call the method we are trying to test
		authorDaoImpl.delete(testAuthor);
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
	public void createTest() throws SQLException {
		authorDaoImpl.delete(testAuthor);
		
		int previousSize = mySQLSize();
		
		testAuthor = authorDaoImpl.create(authorName);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testAuthor.getName(), authorName);
	}
	
	@Test
	public void deleteTest() throws SQLException {
		int previousSize = mySQLSize();
		
		authorDaoImpl.delete(testAuthor);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(authorDaoImpl.get(testAuthor.getId()));
	}
	
	@Test
	public void updateTest() throws SQLException {
		Author newAuthor = new Author(testAuthor.getId(), "Author Person");
		
		authorDaoImpl.update(newAuthor);
		
		Author updatedAuthor = authorDaoImpl.get(newAuthor.getId());
		
		assertNotNull(updatedAuthor);
		assertEquals(newAuthor, updatedAuthor);
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getTest() throws SQLException {
		Author foundAuthor = authorDaoImpl.get(testAuthor.getId());
		assertNotNull(foundAuthor);
		assertEquals(foundAuthor, testAuthor);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundTest() throws SQLException {
		Author foundAuthor = authorDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundAuthor);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Author> listOfAuthors = authorDaoImpl.getAll();
		int authorSize = mySQLSize();
		assertEquals(listOfAuthors.size(), authorSize);
	}
}
