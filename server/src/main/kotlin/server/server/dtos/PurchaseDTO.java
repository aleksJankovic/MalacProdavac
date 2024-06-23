package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


@Getter
@Setter
@Builder
public class PurchaseDTO {
    private String productName;
    private Date date;

    private String measurement;
    private double price;
    private int quantity;
}
