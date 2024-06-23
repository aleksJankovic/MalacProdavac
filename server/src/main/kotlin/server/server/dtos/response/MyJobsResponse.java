package server.server.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MyJobsResponse {
    private Long orderId;
    private String orderDate;
    private double delivererPrice;
    private double purchasePrice;
    private Long offerId;
    private Long offerStatusId;
    private String buyerName;
    private String buyerSurname;
    private String buyerUsername;
    private byte[] buyerPicture;
}
