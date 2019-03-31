package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Borrower;

public interface BorrowerDao extends Dao<Borrower> {
	public abstract Borrower create(String borrowerName, String borrowerAddress, String borrowerPhone) throws SQLException;
}