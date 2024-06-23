package server.server.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DelivererJobResponse {
    private Long orderId;
    private byte[] buyerImage;
    private String buyerUsername;
    private double sellerLong;
    private double sellerLat;
    private String buyerAddress;
    private boolean sentOffer;

}
