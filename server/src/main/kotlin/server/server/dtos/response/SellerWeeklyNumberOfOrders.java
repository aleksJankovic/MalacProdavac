package server.server.dtos.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerWeeklyNumberOfOrders {
    private List<Long> previousWeek;
    private List<Long> currentWeek;
}
