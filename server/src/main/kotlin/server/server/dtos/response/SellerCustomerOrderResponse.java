package server.server.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerCustomerOrderResponse {
    private Long orderId;
    private String username;
    private String address;
    private String date;
    private byte[] picture;
    private Long orderStatus;
}
