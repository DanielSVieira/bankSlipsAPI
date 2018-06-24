package com.bankslips.validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BankSlipsValidator {

	public static List<String> getErrorMessages(BindingResult bindingResult) {
		List<ObjectError> errors = bindingResult.getAllErrors();
		return errors.stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());

	}

	public static boolean jsonContainsErrors(BindingResult bindingResult) {
		return !Objects.isNull(bindingResult) && bindingResult.hasFieldErrors(); 
	}

}
