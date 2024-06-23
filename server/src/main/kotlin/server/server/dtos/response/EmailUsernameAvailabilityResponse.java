package server.server.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailUsernameAvailabilityResponse {
    private boolean isUsernameTaken;
    private boolean isEmailTaken;
}
