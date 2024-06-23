package server.server.exceptions;

public class PibAlreadyTakenException extends RuntimeException{
    public PibAlreadyTakenException(String message){
        super(message);
    }
}
