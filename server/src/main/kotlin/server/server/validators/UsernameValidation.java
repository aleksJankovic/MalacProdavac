package server.server.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import server.server.validators.constraints.UsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface UsernameValidation {
    String message() default "Morate uneti validno korisnicko ime.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
