package server.server.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemsRequest {
    private Long productId;
    private int quantity;
}
