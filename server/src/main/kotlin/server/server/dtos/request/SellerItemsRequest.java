package server.server.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SellerItemsRequest {
    private Long sellerId;
    private String sellerAddress;
    private List<OrderItemsRequest> orderItems;
}
