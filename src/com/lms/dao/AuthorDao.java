package com.lms.dao;

import com.lms.model.Author;

public interface AuthorDao extends Dao<Author> {
	public abstract void create(String authorName);
}
