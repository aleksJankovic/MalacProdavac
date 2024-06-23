package server.server.validators.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import server.server.validators.PasswordValidation;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<PasswordValidation,String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        return Pattern.matches(PASSWORD_PATTERN, s);
    }
}
