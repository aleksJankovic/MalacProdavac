package server.server.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailSent extends Exception{
    private Long orderNumber;
    public MailSent(Long orderNumber, String message){super(message); this.orderNumber = orderNumber;}
}
