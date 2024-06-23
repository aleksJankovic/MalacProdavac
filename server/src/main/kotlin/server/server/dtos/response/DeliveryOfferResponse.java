package server.server.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryOfferResponse {
    private Long offerId;

    private Long delivererId;
    private String delivererUsername;
    private double delivererAvgGrade;
    private byte[] delivererImage;

    private Long offerStatus;
    private Long orderId;
    private double price;
    private String date;


}
