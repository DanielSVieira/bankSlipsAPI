package com.bankslips.contants;

import org.springframework.stereotype.Component;
@Component
public class ErrorMessages {
	public static final String BANKSLIPS_NOT_FOUND = "Bankslip not found with id ";
	public static final String BANKSLIPS_NOT_PROVIDED = "Bankslip not provided in request body";
	public static final String INVALID_FORMAT_PROVIDED = "Invalid bankslip provided.The possible reasons are:\n ○ A field of the provided bankslip was null or with invalid values";
	public static final String INVALID_UUID_PROVIDED = " is an invalid UUID. Please provide a valid id";
	public static final String DUE_DATE_NOT_PROVIDED = "Due date must be informed";
	public static final String DUE_DATE_IN_PAST = "Due date be in present or future. Past due date is not accepted";
	public static final String TOTAL_IN_CENTS_NOT_PROVIDED = "Bank slips value must be informed.";
	public static final String CUSTOMER_NOT_PROVIDED = "Customer must be informed";
	public static final String CUSTOMER_INVALID_SIZE = "Customer must have at least 3 characters and not more than 255 characters";
	public static final String INVALID_LOCAL_DATE_PROVIDED = "Provided date must not be null";
	public static final String DUPLICATED_EXTERNAL_ID = "Duplicated externalId";
	public static final String ERROR = "Error: ";
	public static final String MESSAGE = "Message";
	public static final String MISSING_PATH_VARIABLE = "Missing path variable: %s";
	public static final String INVALID_PATH_TEMPLATE = "Invalid path variable: %s";
	public static final String EXPECTED_TYPE = "Expected type:  %s";
	public static final String VALIDATION_FAILED = "Validation failed";
	public static final String INTERNAL_SERVER_ERROR = "Oops... We found an error, and we are working to fix it as soon as possible";
	public static final String ILLEGAL_ARGUMENTS_ERROR =  "IllegalArgumentException";
	public static final String EMPTY_BANKSLIPS_LIST = "Bulk upload list cannot be empty";
	public static final String JOB_NOT_FOUND = "Job not found. Id: %s";
	public static final String INVALID_BANK_SLIPS_STATUS_CHANGE = "Bank slip status can only be changed if it is pending";
	
}
