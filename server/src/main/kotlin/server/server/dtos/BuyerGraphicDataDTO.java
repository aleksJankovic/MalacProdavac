package server.server.dtos;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerGraphicDataDTO {
    private Long categoryId;
    private Long numberOfOrders;
}
