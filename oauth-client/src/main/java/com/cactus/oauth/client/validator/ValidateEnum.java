package com.cactus.oauth.client.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull(message = "Value cannot be null")
@ReportAsSingleViolation
public @interface ValidateEnum {

	Class<? extends Enum<?>> acceptedValues();

	String message() default "Enum validation failed!!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
