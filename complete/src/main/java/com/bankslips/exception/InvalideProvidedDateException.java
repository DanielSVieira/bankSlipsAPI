package com.bankslips.exception;

public class InvalideProvidedDateException extends RuntimeException {
	
	private static final long serialVersionUID = -6980660577911758172L;

	public InvalideProvidedDateException(String errorMessage) {
		super(errorMessage);
	}
}
