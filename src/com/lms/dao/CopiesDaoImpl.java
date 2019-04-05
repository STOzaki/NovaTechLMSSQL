package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.lms.dao.util.CloseResources;
import com.lms.model.Book;
import com.lms.model.Branch;

public class CopiesDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_book_copies";
	
	public CopiesDaoImpl(Connection conn) {
		this.conn = conn;
	}

	// if no copies, returns 0
	public int getCopies(Branch branch, Book book) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM " + table + " WHERE bookId = ? AND branchId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, book.getId());
			prepareStatement.setInt(2, branch.getId());
			resultSet = prepareStatement.executeQuery();
			
			int returnBookCopies = 0;
			if(resultSet.next()) {
				// mySQL will return 0 if column is mySQL null
				returnBookCopies = resultSet.getInt("noOfCopies");
			}
			return returnBookCopies;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	// if int is 0, delete
	public void setCopies(Branch branch , Book book, int noOfCopies) throws SQLException {
		if(noOfCopies <= 0) {
			delete(branch, book);
		} else {
			if(exist(branch, book)) {
				update(branch, book, noOfCopies);
			} else {
				create(branch, book, noOfCopies);
			}
		}
	}
	
	private void create(Branch branch, Book book, int noOfCopies) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "INSERT INTO " + table + " (bookId, branchId, noOfCopies) VALUES (?, ?, ?);";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, book.getId());
			prepareStatement.setInt(2, branch.getId());
			prepareStatement.setInt(3, noOfCopies);
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	private void update(Branch branch, Book book, int noOfCopies) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "UPDATE " + table + " SET noOfCopies = ? WHERE bookId = ? AND branchId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, noOfCopies);
			prepareStatement.setInt(2, book.getId());
			prepareStatement.setInt(3, branch.getId());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	private boolean exist(Branch branch, Book book) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM " + table + " WHERE bookId = ? AND branchId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, book.getId());
			prepareStatement.setInt(2, branch.getId());
			resultSet = prepareStatement.executeQuery();
			
			boolean foundEntry = false;
			if(resultSet.next()) {
				foundEntry = true;
			}
			return foundEntry;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	private void delete(Branch branch, Book book) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "DELETE FROM " + table + " WHERE bookId = ? AND branchId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, book.getId());
			prepareStatement.setInt(2, branch.getId());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public Map<Book, Integer> getAllBranchCopies(Branch branch) throws SQLException {
		Map<Book, Integer> returnBranchHashMap = new HashMap<Book, Integer>();

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM tbl_book_copies WHERE branchId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, branch.getId());
			resultSet = prepareStatement.executeQuery();
	
			while (resultSet.next()) {
				BookDaoImpl bookDaoImpl = new BookDaoImpl(conn);
				Book foundBook = bookDaoImpl.get(resultSet.getInt("bookId"));
				returnBranchHashMap.put(foundBook, resultSet.getInt("noOfCopies"));
			}
			return returnBranchHashMap;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	public Map<Branch, Integer> getAllBookCopies(Book book) throws SQLException {
		Map<Branch, Integer> returnBookHashMap = new HashMap<Branch, Integer>();

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM tbl_book_copies WHERE bookId = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, book.getId());
			resultSet = prepareStatement.executeQuery();
			
			while (resultSet.next()) {
				LibraryBranchDaoImpl branchDaoImpl = new LibraryBranchDaoImpl(conn);
				Branch foundBranch = branchDaoImpl.get(resultSet.getInt("branchId"));
				returnBookHashMap.put(foundBranch, resultSet.getInt("noOfCopies"));
			}
			return returnBookHashMap;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public Map<Branch, Map<Book, Integer>> getAllCopies() throws SQLException {
		Map<Branch, Map<Book, Integer>> returnBookBranchHashMap = new HashMap<>();

		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM tbl_book_copies;";
			prepareStatement = conn.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery();
			
			while (resultSet.next()) {
				LibraryBranchDaoImpl branchDaoImpl = new LibraryBranchDaoImpl(conn);
				BookDaoImpl bookDaoImpl = new BookDaoImpl(conn);
				Branch foundBranch = branchDaoImpl.get(resultSet.getInt("branchId"));
				Book foundBook = bookDaoImpl.get(resultSet.getInt("bookId"));
				int noOfCopies = resultSet.getInt("noOfCopies");
				
				if(returnBookBranchHashMap.containsKey(foundBranch)) {
					returnBookBranchHashMap.get(foundBranch).put(foundBook, noOfCopies);
				} else {
					Map<Book, Integer> tempHashMap = new HashMap<>();
					tempHashMap.put(foundBook, noOfCopies);
					returnBookBranchHashMap.put(foundBranch, tempHashMap);
				}
			}
			return returnBookBranchHashMap;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

}
