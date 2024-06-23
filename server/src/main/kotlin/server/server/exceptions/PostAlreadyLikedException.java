package server.server.exceptions;

public class PostAlreadyLikedException extends RuntimeException{
    public PostAlreadyLikedException(String message) {
        super(message);
    }
}
