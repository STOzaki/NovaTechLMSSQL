package com.lms.dao;

import com.lms.model.Branch;

public interface LibraryBranchDao extends Dao<Branch> {
	public abstract void create(String branchName, String branchAddress);
}
