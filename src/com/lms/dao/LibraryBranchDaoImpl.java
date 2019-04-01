package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Branch;

public class LibraryBranchDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_library_branch";
	
	public LibraryBranchDaoImpl(Connection conn) {
		this.conn = conn;
	}
	
	// if name or address is null, then it will fill the query with sql null
	public Branch create(String branchName, String branchAddress) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "INSERT INTO " + table + " (branchName, branchAddress) VALUES (?, ?);";
		prepareStatement = conn.prepareStatement(sql);
		
		if(branchName == null) {
			prepareStatement.setNull(1, Types.NULL);
		} else {
			prepareStatement.setString(1, branchName);
		}

		if(branchAddress == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setString(2, branchAddress);
		}

		prepareStatement.executeUpdate();
		
		sql = "SELECT * FROM " + table + " ORDER BY branchId DESC LIMIT 1;";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();

		resultSet.next();
		Branch returnBranch = new Branch(resultSet.getInt("branchId"), resultSet.getString("branchName"),
				resultSet.getString("branchAddress"));
		return returnBranch;
	}
	
	// if name or address is null, then it will fill the query with sql null
	public void update(Branch branch) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "UPDATE " + table + " SET branchName = ?, branchAddress = ?  WHERE branchId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		
		if(branch.getName() == null) {
			prepareStatement.setNull(1, Types.NULL);
		} else {
			prepareStatement.setString(1, branch.getName());
		}

		if(branch.getAddress() == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setString(2, branch.getAddress());
		}
		prepareStatement.setInt(3, branch.getId());
		prepareStatement.executeUpdate();
	}
	
	public void delete(Branch branch) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "DELETE FROM " + table + " WHERE branchId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, branch.getId());
		prepareStatement.executeUpdate();
	}

	public Branch get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM " + table + " WHERE branchId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, id);
		resultSet = prepareStatement.executeQuery();
		
		Branch returnBranch = null;
		if(resultSet.next()) {
			returnBranch = new Branch(resultSet.getInt("branchId"), resultSet.getString("branchName"),
					resultSet.getString("branchAddress"));
		}
		return returnBranch;
	}

	public List<Branch> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<Branch> returnList = new ArrayList<>();
		
		String sql = "SELECT * FROM " + table + ";";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();
		while (resultSet.next()) {
			returnList.add(new Branch(resultSet.getInt("branchId"), resultSet.getString("branchName"),
					resultSet.getString("branchAddress")));
		}
		return returnList;
	}
}
