package server.server.dtos.request;

import lombok.Getter;
import lombok.Setter;
import server.server.validators.PasswordValidation;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    @PasswordValidation
    private String newPassword;
}
