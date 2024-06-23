package server.server.dtos.request.simulator;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulatorOrderItemsRequest {
    private Long productId;
    private int quantity;
}
