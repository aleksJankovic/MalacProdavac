package server.server.validators.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import server.server.validators.UsernameValidation;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<UsernameValidation, String> {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9!@#$%^&*()-_+=ćčžšđĆČŽŠĐ]*$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        return Pattern.matches(USERNAME_PATTERN, s);
    }
}
