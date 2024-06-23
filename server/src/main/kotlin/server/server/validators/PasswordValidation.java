package server.server.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import server.server.validators.constraints.PasswordValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordValidation {
    String message() default "Morate uneti validnu lozinku.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
