package com.lms.service;

import java.util.List;

import com.lms.customExceptions.TransactionException;
import com.lms.model.Branch;

/**
 * A base interface that all service interfaces extend.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public interface Service {
	/**
	 * Get a list (order should not be relied on) of all the library branches in the
	 * database.
	 *
	 * @return all the borrowers in the database.
	 */
	List<Branch> getAllBranches() throws TransactionException;
}
