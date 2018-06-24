package com.bankslips.contants;

public class ErrorMessages {
	public static final String BANKSLIPS_NOT_FOUND = "Bankslip not found with id ";
	public static final String BANKSLIPS_NOT_PROVIDED = "Bankslip not provided in request body";
	public static final String INVALID_FORMAT_PROVIDED = "Invalid bankslip provided.The possible reasons are:\n ○ A field of the provided bankslip was null or with invalid values";
	public static final String INVALID_UUID_PROVIDED = " is an invalid UUID. Please provide a valid id";
	public static final String DUE_DATE_NOT_PROVIDED = "Due date must be informed.";
	public static final String TOTAL_IN_CENTS_NOT_PROVIDED = "Bank slips value must be informed.";
	public static final String CUSTOMER_NOT_PROVIDED = "Customer must be informed";
	public static final String CUSTOMER_INVALID_SIZE = "Customer must have at least 3 characters and not more than 255 characters";
	public static final String INVALID_LOCAL_DATE_PROVIDED = "Provided date must not be null";
	
}
