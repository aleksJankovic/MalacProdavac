package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
public class DelivererDTO {
    private Long   id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private byte[] picture;
    private String role;
    private String location;
    private double longitude;
    private double latitude;
    private double avgGrade;
    private boolean owner;
}
