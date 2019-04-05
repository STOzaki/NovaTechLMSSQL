package com.lms.customExceptions;

@SuppressWarnings("serial")
public class DeleteException extends TransactionException {

	public DeleteException(String errorMessage) {
		super(errorMessage);
	}
	
	public DeleteException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
