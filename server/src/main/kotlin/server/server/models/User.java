package server.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import server.server.validators.PasswordValidation;
import server.server.validators.UsernameValidation;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",nullable = false)
    private Long userId;

    @NotNull(message = "Morate uneti ime")
    @Size(min=2, max=20)
    private String name;

    @NotNull(message = "Morate uneti prezime")
    @Size(min=2, max=20)
    private String surname;

    @NotNull(message = "Morate uneti korisničko ime")
    @UsernameValidation
    private String username;

    @NotNull(message = "Morate uneti lozinku")
    @PasswordValidation
    private String password;

    @NotNull(message = "Morate uneti email")
    @Email(message = "Morate uneti validnu email adresu", regexp = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})")
    private String email;

    @Lob
    private String picture;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private double longitudeB;
    private double latitudeB;
}
