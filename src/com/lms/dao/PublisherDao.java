package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Publisher;

public interface PublisherDao extends Dao<Publisher> {
	public abstract Publisher create(String publisherName, String publisherAddress, String publisherPhone) throws SQLException;
}
