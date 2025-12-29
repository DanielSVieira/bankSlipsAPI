package com.bankslips.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bankslips.contants.ErrorMessages;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class BankSlipsExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage() + ErrorMessages.BANKSLIPS_NOT_PROVIDED);
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
	
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> handleMissingPathVariable(MissingPathVariableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ErrorMessages.ERROR, String.format(ErrorMessages.MISSING_PATH_VARIABLE, ex.getVariableName()));
        error.put(ErrorMessages.MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ErrorMessages.ERROR, ErrorMessages.VALIDATION_FAILED);
        error.put(ErrorMessages.MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDatabaseConnectionException(DataAccessException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ErrorMessages.ERROR, ErrorMessages.INTERNAL_SERVER_ERROR);
        error.put(ErrorMessages.MESSAGE, ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
    
    @ExceptionHandler(InvalidBulkUploadException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBulk(InvalidBulkUploadException ex) {
        return ResponseEntity.badRequest().body(Map.of(ErrorMessages.MESSAGE, ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(InvalidBulkUploadException ex) {
        return ResponseEntity.badRequest().body(Map.of(ErrorMessages.MESSAGE, ex.getMessage()));
    }    
    
    @ExceptionHandler(InvalidBankSlipsStatusChangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBankSlipsStatusChangeException(InvalidBankSlipsStatusChangeException ex) {
        return ResponseEntity.badRequest().body(Map.of(ErrorMessages.MESSAGE, ex.getMessage()));
    }   
    

}
