package com.lms.dao;

import java.sql.SQLException;

import com.lms.model.Branch;

public interface LibraryBranchDao extends Dao<Branch> {
	public abstract void create(String branchName, String branchAddress) throws SQLException;
}
