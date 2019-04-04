package com.lms.customExceptions;

@SuppressWarnings("serial")
public class CriticalSQLException extends TransactionException {

	public CriticalSQLException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
