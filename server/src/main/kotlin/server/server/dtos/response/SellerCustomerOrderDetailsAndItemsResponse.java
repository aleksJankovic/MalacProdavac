package server.server.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SellerCustomerOrderDetailsAndItemsResponse {
    private Long orderId;
    private String buyerName;
    private String buyerSurname;
    private String buyerAddress;
    private double buyerLong;
    private double buyerLat;
    private String buyerEmail;
    private String buyerPhoneNumber;
    private Long shippingMethodId;
    private Long paymentMethodId;
    private double sellerLong;
    private double sellerLat;
    private List<PurchaseOrderItemsResponse> purchaseItems;
    private String comment;
    private String date_time;
    private double price;
}
