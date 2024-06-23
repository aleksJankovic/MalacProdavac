package server.server.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String email;

    private byte[] picture;

    private String role;

    //Za dostavljaca
    private ArrayList<Long> licenceCategories;

    //Za prodavca
    private String pib;
    private double longitude;
    private double latitude;
    private String accountNumber;
}
