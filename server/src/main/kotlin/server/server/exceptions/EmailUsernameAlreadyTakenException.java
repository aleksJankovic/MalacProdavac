package server.server.exceptions;

import lombok.Getter;
import lombok.Setter;
import server.server.dtos.response.EmailUsernameAvailabilityResponse;

@Getter
@Setter
public class EmailUsernameAlreadyTakenException extends RuntimeException{
    private EmailUsernameAvailabilityResponse response;
    public EmailUsernameAlreadyTakenException(String message){
        super(message);
    }
    public EmailUsernameAlreadyTakenException(String message, EmailUsernameAvailabilityResponse response){
        super(message);
        this.response = response;
    }
}
