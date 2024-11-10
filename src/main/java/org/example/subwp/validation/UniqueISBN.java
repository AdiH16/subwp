package org.example.subwp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.subwp.validation.UniqueISBNValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueISBNValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueISBN {
    String message() default "ISBN već postoji!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
