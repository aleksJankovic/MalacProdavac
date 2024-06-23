package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    private String productName;
    private double unitPrice;
    private int quantity;
    private String measurement;

    public double calculateTotalProductAmount(){
        return unitPrice * quantity;
    }
}
