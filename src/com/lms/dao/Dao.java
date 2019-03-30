package com.lms.dao;

import java.util.List;

public interface Dao <T> {
	public abstract void update(T t);
	public abstract void delete(T t);
	public abstract T get(int id);
	public abstract List<T> getAll();
}
