package com.lms.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao <T> {
	public abstract void update(T t) throws SQLException;
	public abstract void delete(T t) throws SQLException;
	public abstract T get(int id) throws SQLException;
	public abstract List<T> getAll() throws SQLException;
}
