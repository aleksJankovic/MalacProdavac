package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderDTO {
    private Long id;
    private SellerDTO sellerDTO;
    private List<PurchaseDTO> purchaseDTOS;
}
