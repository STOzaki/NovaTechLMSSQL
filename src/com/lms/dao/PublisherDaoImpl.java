package com.lms.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.lms.model.Publisher;

public class PublisherDaoImpl {
	private final Connection conn;
	private final static String table = "tbl_publisher";
	
	public PublisherDaoImpl(Connection conn) {
		this.conn = conn;
	}
	
	// if address or phone is null, then it will fill the query with sql null
	public Publisher create(String publisherName, String publisherAddress, String publisherPhone) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "INSERT INTO " + table + " (publisherName, publisherAddress, publisherPhone) VALUES (?, ?, ?);";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, publisherName);

		if(publisherAddress == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setString(2, publisherAddress);
		}

		if(publisherPhone == null) {
			prepareStatement.setNull(3, Types.NULL);
		} else {
			prepareStatement.setString(3, publisherPhone);
		}

		prepareStatement.executeUpdate();
		
		sql = "SELECT * FROM " + table + " ORDER BY publisherId DESC LIMIT 1;";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();

		resultSet.next();
		Publisher returnPublisher = new Publisher(resultSet.getInt("publisherId"), resultSet.getString("publisherName"),
				resultSet.getString("publisherAddress"), resultSet.getString("publisherPhone"));
		return returnPublisher;
	}
	
	// if address or phone is null, then it will fill the query with sql null
	public void update(Publisher publisher) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "UPDATE " + table + " SET publisherName = ?, publisherAddress = ?, publisherPhone = ?  WHERE publisherId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setString(1, publisher.getName());
		
		if(publisher.getAddress() == null) {
			prepareStatement.setNull(2, Types.NULL);
		} else {
			prepareStatement.setString(2, publisher.getAddress());
		}
		
		if(publisher.getPhone() == null) {
			prepareStatement.setNull(3, Types.NULL);
		} else {
			prepareStatement.setString(3, publisher.getPhone());
		}
		prepareStatement.setInt(4, publisher.getId());
		prepareStatement.executeUpdate();
	}
	
	public void delete(Publisher publisher) throws SQLException {
		PreparedStatement prepareStatement = null;
		String sql = "DELETE FROM " + table + " WHERE publisherId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, publisher.getId());
		prepareStatement.executeUpdate();
	}

	public Publisher get(int id) throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String sql = "SELECT * FROM " + table + " WHERE publisherId = ?;";
		prepareStatement = conn.prepareStatement(sql);
		prepareStatement.setInt(1, id);
		resultSet = prepareStatement.executeQuery();
		
		Publisher returnPublisher = null;
		if(resultSet.next()) {
			returnPublisher = new Publisher(resultSet.getInt("publisherId"), resultSet.getString("publisherName"),
					resultSet.getString("publisherAddress"), resultSet.getString("publisherPhone"));
		}
		return returnPublisher;
	}

	public List<Publisher> getAll() throws SQLException {
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<Publisher> returnList = new ArrayList<>();
		
		String sql = "SELECT * FROM " + table + ";";
		prepareStatement = conn.prepareStatement(sql);
		resultSet = prepareStatement.executeQuery();
		while (resultSet.next()) {
			returnList.add(new Publisher(resultSet.getInt("publisherId"), resultSet.getString("publisherName"),
					resultSet.getString("publisherAddress"), resultSet.getString("publisherPhone")));
		}
		return returnList;
	}
}
