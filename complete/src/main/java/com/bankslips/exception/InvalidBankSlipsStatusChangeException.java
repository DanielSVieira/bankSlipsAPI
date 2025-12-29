package com.bankslips.exception;

public class InvalidBankSlipsStatusChangeException extends RuntimeException {
	
	private static final long serialVersionUID = 1048688336603848233L;

	public InvalidBankSlipsStatusChangeException (String errorMessage) { 
		super(errorMessage);
	}

}
