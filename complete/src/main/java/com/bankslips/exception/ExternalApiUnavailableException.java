package com.bankslips.exception;

public class ExternalApiUnavailableException extends Exception {
	
	private static final long serialVersionUID = -6980660577911758172L;

	public ExternalApiUnavailableException(String errorMessage) {
		super(errorMessage);
	}
	

}
