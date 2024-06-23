package server.server.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import server.server.validators.PasswordValidation;
import server.server.validators.UsernameValidation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelivererUpdateRequest {
    @Pattern(regexp = "^[a-zA-Z]{2,20}$", message = "Ime može sadržati samo slova, između 2 i 20 karaktera")
    @Size(min = 2, max = 20)
    private String name;

    @Pattern(regexp = "^[a-zA-Z]{2,20}$", message = "Ime može sadržati samo slova, između 2 i 20 karaktera")
    @Size(min = 2, max = 20)
    private String surname;

    @UsernameValidation
    private String username;

    @Email(message = "Morate uneti validnu email adresu", regexp = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})")
    private String email;

    @PasswordValidation
    private String password;

    private double longitude;
    private double latitude;

    private byte[] picture;
}
