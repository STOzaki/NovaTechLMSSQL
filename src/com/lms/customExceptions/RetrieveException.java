package com.lms.customExceptions;

@SuppressWarnings("serial")
public class RetrieveException extends TransactionException {

	public RetrieveException(String errorMessage) {
		super(errorMessage);
	}

	public RetrieveException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
