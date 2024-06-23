package server.server.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangeOrderStatusRequest {
    Long orderId;
    Long statusId;
}
