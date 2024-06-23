package server.server.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemsResponse {
    private Long productCategoryId;
    private String productName;
    private byte[] productImage;
    private double productPrice;
    private int quantity;
    private String measurement;
    private String measurementValue;
    private Long productId;
}
