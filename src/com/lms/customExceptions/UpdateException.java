package com.lms.customExceptions;

@SuppressWarnings("serial")
public class UpdateException extends TransactionException {

	public UpdateException(String errorMessage) {
		super(errorMessage);
	}
	
	public UpdateException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
