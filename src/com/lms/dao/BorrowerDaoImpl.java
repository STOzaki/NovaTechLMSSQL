package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.lms.dao.util.CloseResources;
import com.lms.model.Borrower;

public class BorrowerDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_borrower";
	
	public BorrowerDaoImpl(Connection conn) {
		this.conn = conn;
	}
	
	public Borrower create(String borrowerName, String borrowerAddress, String borrowerPhone) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "INSERT INTO " + table + " (name, address, phone) VALUES (?, ?, ?);";
			prepareStatement = conn.prepareStatement(sql);
			
			if(borrowerName == null) {
				prepareStatement.setNull(1, Types.NULL);
			} else {
				prepareStatement.setString(1, borrowerName);
			}
			
			if(borrowerAddress == null) {
				prepareStatement.setNull(2, Types.NULL);
			} else {
				prepareStatement.setString(2, borrowerAddress);
			}
			
			if(borrowerPhone == null) {
				prepareStatement.setNull(3, Types.NULL);
			} else {
				prepareStatement.setString(3, borrowerPhone);
			}
			
			prepareStatement.executeUpdate();
			
			// close first prepareStatement
			prepareStatement.close();
			
			sql = "SELECT * FROM " + table + " ORDER BY cardNo DESC LIMIT 1;";
			prepareStatement = conn.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery();
	
			resultSet.next();
			Borrower returnBorrower = new Borrower(resultSet.getInt("cardNo"), resultSet.getString("name"),
					resultSet.getString("address"), resultSet.getString("phone"));
			return returnBorrower;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	// if address or phone is null, then it will fill the query with sql null
	public void update(Borrower borrower) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "UPDATE " + table + " SET name = ?, address = ?, phone = ?  WHERE cardNo = ?;";
			prepareStatement = conn.prepareStatement(sql);
			
			if(borrower.getName() == null) {
				prepareStatement.setNull(1, Types.NULL);
			} else {
				prepareStatement.setString(1, borrower.getName());
			}
			
			if(borrower.getAddress() == null) {
				prepareStatement.setNull(2, Types.NULL);
			} else {
				prepareStatement.setString(2, borrower.getAddress());
			}
			
			if(borrower.getPhone() == null) {
				prepareStatement.setNull(3, Types.NULL);
			} else {
				prepareStatement.setString(3, borrower.getPhone());
			}
			prepareStatement.setInt(4, borrower.getCardNo());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}
	
	public void delete(Borrower borrower) throws SQLException {
		PreparedStatement prepareStatement = null;
		try {
			String sql = "DELETE FROM " + table + " WHERE cardNo = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, borrower.getCardNo());
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public Borrower get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "SELECT * FROM " + table + " WHERE cardNo = ?;";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, id);
			resultSet = prepareStatement.executeQuery();
			
			Borrower returnBorrower = null;
			if(resultSet.next()) {
				returnBorrower = new Borrower(resultSet.getInt("cardNo"), resultSet.getString("name"),
						resultSet.getString("address"), resultSet.getString("phone"));
			}
			return returnBorrower;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			CloseResources.closeResultSet(resultSet);
			CloseResources.closePreparedStatement(prepareStatement);
		}
	}

	public List<Borrower> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			List<Borrower> returnList = new ArrayList<>();
			
			String sql = "SELECT * FROM " + table + ";";
			prepareStatement = conn.prepareStatement(sql);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				returnList.add(new Borrower(resultSet.getInt("cardNo"), resultSet.getString("name"),
						resultSet.getString("address"), resultSet.getString("phone")));
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
