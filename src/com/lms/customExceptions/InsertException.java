package com.lms.customExceptions;

@SuppressWarnings("serial")
public class InsertException extends TransactionException {

	public InsertException(String errorMessage) {
		super(errorMessage);
	}
}
