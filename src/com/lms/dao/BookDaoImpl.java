package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;

public class BookDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_book";
	
	public BookDaoImpl(Connection conn) {
		this.conn = conn;
	}
	
	public Book create(String title, Author author, Publisher publisher) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "INSERT INTO " + table + " (title, authId, pubId) VALUES (?, ?, ?);";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, title);
		
		if(author == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setInt(2, author.getId());
		}
		
		if(publisher == null) {
			prepareStatement.setNull(3, Types.NULL);
		} else {
			prepareStatement.setInt(3, publisher.getId());
		}
		
		prepareStatement.executeUpdate();
		
		sql = "SELECT * FROM " + table + " ORDER BY bookId DESC LIMIT 1;";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();

		resultSet.next();
		Book returnBook = new Book(resultSet.getInt("bookId"), resultSet.getString("title"), author, publisher);
		return returnBook;
	}
	
	// if address or phone is null, then it will fill the query with sql null
	public void update(Book book) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "UPDATE " + table + " SET title = ?, authId = ?, pubId = ?  WHERE bookId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, book.getTitle());
		
		if(book.getAuthor() == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setInt(2, book.getAuthor().getId());
		}
		
		if(book.getPublisher() == null) {
			prepareStatement.setNull(3, Types.NULL);
		} else {
			prepareStatement.setInt(3, book.getPublisher().getId());
		}
		prepareStatement.setInt(4, book.getId());
		prepareStatement.executeUpdate();
	}
	
	public void delete(Book book) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "DELETE FROM " + table + " WHERE bookId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, book.getId());
		prepareStatement.executeUpdate();
	}

	public Book get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM " + table + " WHERE bookId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, id);
		resultSet = prepareStatement.executeQuery();
		
		Book returnBook = null;
		if(resultSet.next()) {

			Author author = null;
			// mySQL will return 0 if column is mySQL null
			if(resultSet.getInt("authId") != 0) {
				AuthorDaoImpl authorDaoImpl = new AuthorDaoImpl(conn);
				int authorId = resultSet.getInt("authId");
				author = authorDaoImpl.get(authorId);
			}
			
			Publisher publisher = null;
			// mySQL will return 0 if column is mySQL null
			if(resultSet.getInt("pubId") != 0) {
				PublisherDaoImpl publisherDaoImpl = new PublisherDaoImpl(conn);
				int publisherId = resultSet.getInt("pubId");
				publisher = publisherDaoImpl.get(publisherId);
			}
			
			returnBook = new Book(resultSet.getInt("bookId"), resultSet.getString("title"),
					author, publisher);
		}
		return returnBook;
	}

	public List<Book> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<Book> returnList = new ArrayList<>();
		
		String sql = "SELECT * FROM " + table + ";";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();
		while (resultSet.next()) {

			Author author = null;
			// mySQL will return 0 if column is mySQL null
			if(resultSet.getInt("authId") != 0) {
				AuthorDaoImpl authorDaoImpl = new AuthorDaoImpl(conn);
				int authorId = resultSet.getInt("authId");
				author = authorDaoImpl.get(authorId);
			}
			
			Publisher publisher = null;
			// mySQL will return 0 if column is mySQL null
			if(resultSet.getInt("pubId") != 0) {
				PublisherDaoImpl publisherDaoImpl = new PublisherDaoImpl(conn);
				int publisherId = resultSet.getInt("pubId");
				publisher = publisherDaoImpl.get(publisherId);
			}
			
			returnList.add(new Book(resultSet.getInt("bookId"), resultSet.getString("title"),
					author, publisher));
		}
		return returnList;
	}
}
