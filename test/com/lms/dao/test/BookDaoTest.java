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
import com.lms.dao.AuthorDaoImpl;
import com.lms.dao.BookDaoImpl;
import com.lms.dao.PublisherDaoImpl;
import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.lms.service.util.ConnectingToDataBase;

public class BookDaoTest {
	private String title = "The Book Title";
	
	private String publisherName = "The Publisher";
	private String publisherAddress = "601 New Jersey Ave, Washington, DC 20001";
	private String publisherPhone = "1234567890";
	
	private String authorName = "Author Name";
	
	private static Connection conn = null;

	private static BookDaoImpl bookDaoImpl;
	private static PublisherDaoImpl publisherDaoImpl;
	private static AuthorDaoImpl authorDaoImpl;
	private Book testBook;
	private Author testAuthor;
	private Publisher testPublisher;
	private static String table = "tbl_book";
	private static String tableId = "bookId";
	
	@BeforeAll
	public static void initAll() throws IOException, SQLException, CriticalSQLException {
		conn = ConnectingToDataBase.connectingToDataBase("test");
		bookDaoImpl = new BookDaoImpl(conn);
		publisherDaoImpl = new PublisherDaoImpl(conn);
		authorDaoImpl = new AuthorDaoImpl(conn);
	}
	
	@AfterAll
	public static void cleanUp() throws IOException, SQLException, CriticalSQLException {
		ConnectingToDataBase.closingConnection(conn);
	}
	
	@BeforeEach
	public void init() throws SQLException {
		testAuthor = authorDaoImpl.create(authorName);
		testPublisher = publisherDaoImpl.create(publisherName, publisherAddress, publisherPhone);
		testBook = bookDaoImpl.create(title, testAuthor, testPublisher);
	}
	
	@AfterEach
	public void tearThis() throws SQLException {
		authorDaoImpl.delete(testAuthor);
		publisherDaoImpl.delete(testPublisher);
		bookDaoImpl.delete(testBook);
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
	public void createBookTest() throws SQLException {
		bookDaoImpl.delete(testBook);
		
		int previousSize = mySQLSize();
		
		testBook = bookDaoImpl.create(title, testAuthor, testPublisher);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize < currentSize);
		assertEquals(testBook.getTitle(), title);
		assertEquals(testBook.getAuthor(), testAuthor);
		assertEquals(testBook.getPublisher(), testPublisher);
	}
	
	@Test
	public void deleteBookTest() throws SQLException {
		int previousSize = mySQLSize();
		
		bookDaoImpl.delete(testBook);
		
		int currentSize = mySQLSize();
		
		assertTrue(previousSize > currentSize);
		assertNull(bookDaoImpl.get(testBook.getId()));
	}

	@DisplayName("Update Correctly")
	@Test
	public void updateBookTest() throws SQLException {
		String newTitle = "New Title";
		String newAuthorName = "New Author Name";
		String newPublisherName = "New Publisher Name";
		String newPublisherAddress = "New Address";
		String newPublisherPhone = "4567891230";
		
		Author newAuthor = authorDaoImpl.create(newAuthorName);
		Publisher newPublisher = publisherDaoImpl.create(newPublisherName,
				newPublisherAddress, newPublisherPhone);
		Book newBook = new Book(testBook.getId(), newTitle, newAuthor, newPublisher);
		
		bookDaoImpl.update(newBook);
		
		Book updatedbook = bookDaoImpl.get(newBook.getId());
		
		assertNotNull(updatedbook);
		assertEquals(newBook, updatedbook);
	}
	
	@DisplayName("Update even if author is null")
	@Test
	public void updateWithAddressNullTest() throws SQLException {
		String newTitle = "New Title";
		String newPublisherName = "New Publisher Name";
		String newPublisherAddress = "New Address";
		String newPublisherPhone = "4567891230";

		Publisher newPublisher = publisherDaoImpl.create(newPublisherName,
				newPublisherAddress, newPublisherPhone);

		Book newBook = new Book(testBook.getId(), newTitle, null, newPublisher);
		
		bookDaoImpl.update(newBook);
		
		Book updatedBook = bookDaoImpl.get(newBook.getId());
		
		assertNotNull(updatedBook);
		assertEquals(newBook, updatedBook);
		assertNull(updatedBook.getAuthor());
	}

	@DisplayName("Update even if publisher is null")
	@Test
	public void updateWithPhoneNullTest() throws SQLException {
		String newTitle = "New Title";
		String newAuthorName = "New Author Name";

		Author newAuthor = authorDaoImpl.create(newAuthorName);

		Book newBook = new Book(testBook.getId(), newTitle, newAuthor, null);
		
		bookDaoImpl.update(newBook);
		
		Book updatedBook = bookDaoImpl.get(newBook.getId());
		
		assertNotNull(updatedBook);
		assertEquals(newBook, updatedBook);
		assertNull(updatedBook.getPublisher());
	}
	
	@DisplayName("Get correctly")
	@Test
	public void getBookTest() throws SQLException {
		Book foundBook = bookDaoImpl.get(testBook.getId());
		assertNotNull(foundBook);
		assertEquals(foundBook, testBook);
	}
	
	@DisplayName("Return null if entry not found")
	@Test
	public void getNotFoundBookTest() throws SQLException {
		Book foundBook = bookDaoImpl.get(Integer.MAX_VALUE);
		assertNull(foundBook);
	}
	
	@Test
	public void getAllTest() throws SQLException {
		List<Book> listOfBooks = bookDaoImpl.getAll();
		int bookSize = mySQLSize();
		assertEquals(listOfBooks.size(), bookSize);
	}

}
