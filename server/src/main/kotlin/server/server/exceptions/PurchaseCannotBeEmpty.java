package server.server.exceptions;

public class PurchaseCannotBeEmpty extends RuntimeException{
    public PurchaseCannotBeEmpty(String message){
        super(message);
    }
}
