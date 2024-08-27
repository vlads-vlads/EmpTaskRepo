package com.example.emp.business.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptyLocalDateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyLocalDate {
    String message() default "Date cannot be null or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}