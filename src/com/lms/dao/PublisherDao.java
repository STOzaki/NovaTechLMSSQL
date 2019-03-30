package com.lms.dao;

import com.lms.model.Publisher;

public interface PublisherDao extends Dao<Publisher> {
	public abstract void create(String publisherName, String publisherAddress, String publisherPhone);
}
