package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.lms.model.Author;

public final class AuthorDaoImpl implements AuthorDao {
	private final Connection conn;
	private final static String table = "tbl_author";
	
	public AuthorDaoImpl(Connection conn) {
		this.conn = conn;
	}

	public Author create(String authorName) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "INSERT INTO " + table + " (authorName) VALUES (?);";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, authorName);
		prepareStatement.executeUpdate();
		
		sql = "SELECT * FROM " + table + " ORDER BY authorId DESC LIMIT 1;";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();

		resultSet.next();
		Author returnAuthor = new Author(resultSet.getInt("authorId"), resultSet.getString("authorName"));
		return returnAuthor;
	}
	

	public void update(Author author) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "UPDATE " + table + " SET authorName = ? WHERE authorId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, author.getName());
		prepareStatement.setInt(2, author.getId());
		prepareStatement.executeUpdate();
	}
	
	public void delete(Author author) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "DELETE FROM " + table + " WHERE authorId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, author.getId());
		prepareStatement.executeUpdate();
	}

	public Author get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM " + table + " WHERE authorId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, id);
		resultSet = prepareStatement.executeQuery();
		
		Author returnAuthor = null;
		if(resultSet.next()) {
			returnAuthor = new Author(resultSet.getInt("authorId"), resultSet.getString("authorName"));
		}
		return returnAuthor;
	}

	public List<Author> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<Author> returnList = new ArrayList<>();
		
		String sql = "SELECT * FROM " + table + ";";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();
		while (resultSet.next()) {
			returnList.add(new Author(resultSet.getInt("authorId"), resultSet.getString("authorName")));
		}
		return returnList;
	}
}
