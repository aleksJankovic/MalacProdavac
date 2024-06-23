package server.server.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderRequest {
    //Buyer details
    private Long buyerLongitude;
    private double buyerLatitude;
    private String buyerAddress;
    private String phoneNumber;

    //Order details
    private Long paymentMethodId;
    private Long shippingMethodId;

    List<SellerItemsRequest> purchase;
}
