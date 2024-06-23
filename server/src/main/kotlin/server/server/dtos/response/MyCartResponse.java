package server.server.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MyCartResponse {
    private Long orderId;
    private String orderDate;
    private double totalPrice;
    private Long orderStatusId;
    private String sellerName;
    private byte[] sellerPicture;
}
