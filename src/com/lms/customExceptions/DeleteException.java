package com.lms.customExceptions;

@SuppressWarnings("serial")
public class DeleteException extends TransactionException {

	public DeleteException(String errorMessage) {
		super(errorMessage);
	}
}
