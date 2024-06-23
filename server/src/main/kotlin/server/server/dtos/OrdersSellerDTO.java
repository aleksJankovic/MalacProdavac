package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrdersSellerDTO {
    private Long id;
    private BuyerDTO buyerDTO;
    private List<PurchaseDTO> purchaseDTOS;
}
