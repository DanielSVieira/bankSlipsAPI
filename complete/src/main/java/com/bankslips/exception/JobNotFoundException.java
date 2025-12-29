package com.bankslips.exception;

public class JobNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -5244546641625124735L;

	public JobNotFoundException(String errorMessage) {
		super(errorMessage);
	}

}
