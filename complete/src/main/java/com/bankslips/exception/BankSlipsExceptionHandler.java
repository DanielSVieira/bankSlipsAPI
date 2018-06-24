package com.bankslips.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bankslips.contants.ErrorMessages;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class BankSlipsExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessages.BANKSLIPS_NOT_PROVIDED);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<?> handleInvalidFormatException(InvalidFormatException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(ErrorMessages.INVALID_FORMAT_PROVIDED);
	}

	@ExceptionHandler(BankSlipsContraintViolationException.class)
	public ResponseEntity<?> handleBankSlipsContraintViolationException(BankSlipsContraintViolationException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
	}

	@ExceptionHandler(BankSlipsNotFoundException.class)
	public ResponseEntity<?> handleBankSlipsNotFoundException(BankSlipsNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleIllegalArgumentException(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getValue() + ErrorMessages.INVALID_UUID_PROVIDED);
	}


}
