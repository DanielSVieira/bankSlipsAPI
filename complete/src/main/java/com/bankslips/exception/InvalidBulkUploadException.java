package com.bankslips.exception;

public class InvalidBulkUploadException extends RuntimeException {
    private static final long serialVersionUID = 5710590901773634943L;

	public InvalidBulkUploadException(String message) {
        super(message);
    }
}
