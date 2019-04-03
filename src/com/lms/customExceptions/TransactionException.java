package com.lms.customExceptions;

@SuppressWarnings("serial")
public class TransactionException extends Exception {

	public TransactionException(String errorMessage) {
		super(errorMessage);
	}
}
