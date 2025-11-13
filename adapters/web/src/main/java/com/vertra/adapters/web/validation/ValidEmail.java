package com.vertra.adapters.web.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.PARAMETER,})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid email format";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
