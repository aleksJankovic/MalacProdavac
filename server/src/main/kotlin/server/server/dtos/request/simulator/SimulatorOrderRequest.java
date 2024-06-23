package server.server.dtos.request.simulator;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulatorOrderRequest {
    //Buyer details
    private Long buyerId;
    private Long buyerLongitude;
    private double buyerLatitude;
    private String phoneNumber;
    private String address;

    //Seller details
    private Long sellerId;
    //Order details
    private Long orderStatusId;
    private Long shippingMethodId;
    private Long paymentMethodId;

    List<SimulatorOrderItemsRequest> purchase;
}
