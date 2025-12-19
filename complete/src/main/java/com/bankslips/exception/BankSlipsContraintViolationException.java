package com.bankslips.exception;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;

public class BankSlipsContraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 2711032355360617307L;
	public static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;
	private int errorCode;

	public BankSlipsContraintViolationException(String message, HttpStatus errorCode) {
		this(message, errorCode, null);
	}

	public BankSlipsContraintViolationException(String message, HttpStatus httpStatus,
			Set<? extends ConstraintViolation<?>> constraintViolations) {
		super(message, constraintViolations);
		this.errorCode = httpStatus.value();
	}

	public int getStatus() {
		return errorCode;
	}

	public int getErrorCode() {
	    return errorCode;
	}
}
