package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.lms.dao.util.CloseResources;
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
		try {
			String sql = "INSERT INTO " + table + " (authorName) VALUES (?);";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setString(1, authorName);
			prepareStatement.executeUpdate();
			
			// close first one
			prepareStatement.close();

			sql = "SELECT * FROM " + table + " ORDER BY authorId DESC LIMIT 1;";
			prepareStatement = conn.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery();
	
			resultSet.next();
			Author returnAuthor = new Author(resultSet.getInt("authorId"), resultSet.getString("authorName"));
			return returnAuthor;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	

	public void update(Author author) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "UPDATE " + table + " SET authorName = ? WHERE authorId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setString(1, author.getName());
			prepareStatement.setInt(2, author.getId());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	public void delete(Author author) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "DELETE FROM " + table + " WHERE authorId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, author.getId());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public Author get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM " + table + " WHERE authorId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, id);
			resultSet = prepareStatement.executeQuery();
			
			Author returnAuthor = null;
			if(resultSet.next()) {
				returnAuthor = new Author(resultSet.getInt("authorId"), resultSet.getString("authorName"));
			}
			return returnAuthor;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public List<Author> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			List<Author> returnList = new ArrayList<>();
			
			String sql = "SELECT * FROM " + table + ";";
			prepareStatement = conn.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				returnList.add(new Author(resultSet.getInt("authorId"), resultSet.getString("authorName")));
			}
			return returnList;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
}
