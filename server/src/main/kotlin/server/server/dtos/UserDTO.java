package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {
    private Long   id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private byte[] picture;
    private String role;
    private int numberOfFollows;
    private int numberOfOrders;
}
