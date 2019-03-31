package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Author;

public interface AuthorDao extends Dao<Author> {
	public abstract Author create(String authorName) throws SQLException;
}