package server.server.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DeliveryRatingRequest {
    private Long orderId;
    private double grade;
    private String comment;
}
