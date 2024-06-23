package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobDTO {
    private Long id;
    private byte[] picture;
    private String sellerUsername;
    double longitudeStart;
    double latitudeStart;
    double longitudeEnd;
    double latitudeEnd;
}
