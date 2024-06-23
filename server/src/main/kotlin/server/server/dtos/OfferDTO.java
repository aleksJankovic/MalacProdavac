package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OfferDTO {
    private Long offerId;
    private Long orderId;
    private Long delivererId;
    private String orderDate;
    private double delivererPrice;
    private double orderPrice;
    private Long offerStatusId;
    private String delivererName;
    private String delivererSurname;
    private String delivererUsername;
    private byte[] delivererPicture;
}
