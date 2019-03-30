package com.lms.dao;

import com.lms.model.Borrower;

public interface BorrowerDao extends Dao<Borrower> {
	public abstract void create(String borrowerName, String borrowerAddress, String borrowerPhone);
}
