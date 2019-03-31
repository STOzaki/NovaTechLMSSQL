package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Author;

public interface AuthorDao extends Dao<Author> {
	public abstract void create(String authorName) throws SQLException;
}
