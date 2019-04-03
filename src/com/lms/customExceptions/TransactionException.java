package com.lms.customExceptions;

@SuppressWarnings("serial")
public class TransactionException extends Exception {

	protected TransactionException(String errorMessage) {
		super(errorMessage);
	}
}
