package com.cactus.oauth.client.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {

	List<String> valueList = null;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return valueList.contains(value.toUpperCase());
	}

	@Override
	public void initialize(ValidateEnum constraintAnnotation) {
		valueList = new ArrayList<String>();
		Class<? extends Enum<?>> enumClass = constraintAnnotation.acceptedValues();

		@SuppressWarnings("rawtypes")
		Enum[] enumValArr = enumClass.getEnumConstants();

		for (@SuppressWarnings("rawtypes")
		Enum enumVal : enumValArr) {
			valueList.add(enumVal.toString().toUpperCase());
		}
	}

}
