package server.server.exceptions;

public class InvalidProductIdException extends RuntimeException{
    public InvalidProductIdException(String message){
        super(message);
    }
}
