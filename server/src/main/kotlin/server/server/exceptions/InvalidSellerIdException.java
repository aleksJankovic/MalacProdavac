package server.server.exceptions;

public class InvalidSellerIdException extends RuntimeException{
    public InvalidSellerIdException(String message) {
        super(message);
    }
}
