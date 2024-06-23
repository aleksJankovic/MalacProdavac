package server.server.exceptions;

import lombok.Getter;
import lombok.Setter;
import server.server.dtos.response.LoginFailedResponse;

@Getter
@Setter
public class InvalidLoginCredentialsException extends RuntimeException{
    private LoginFailedResponse loginFailedResponse;
    public InvalidLoginCredentialsException(String message){
        super(message);
    }
    public InvalidLoginCredentialsException(String message, LoginFailedResponse loginFailedResponse){
        super(message);
        this.loginFailedResponse = loginFailedResponse;
    }
}
