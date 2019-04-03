package com.lms.customExceptions;

@SuppressWarnings("serial")
public class UnknownSQLException extends TransactionException {

	public UnknownSQLException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
