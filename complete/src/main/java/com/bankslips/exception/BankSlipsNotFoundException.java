package com.bankslips.exception;


public class BankSlipsNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -6980660577911758172L;

	public BankSlipsNotFoundException(String errorMessage) {
		super(errorMessage);
	}
	
}
