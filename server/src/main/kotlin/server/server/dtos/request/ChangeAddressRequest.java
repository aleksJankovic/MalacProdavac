package server.server.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChangeAddressRequest {
    private double longitude;
    private double latitude;
}
