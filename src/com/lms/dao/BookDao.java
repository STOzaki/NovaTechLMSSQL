package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Author;
import com.lms.model.Book;
import com.lms.model.Publisher;

public interface BookDao extends Dao<Book> {
	public abstract void create(String title, Author author, Publisher publisher) throws SQLException;
}
