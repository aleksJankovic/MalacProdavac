package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExploreProductDTO {
    private Long categoryId;
    private Long productId;
    private String productName;
    private byte[] picture;
}
