package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerAddressDTO {
    private Long seller_id;
    private String username;
    private byte[] picture;
    private double latitude;
    private double longitude;
}
