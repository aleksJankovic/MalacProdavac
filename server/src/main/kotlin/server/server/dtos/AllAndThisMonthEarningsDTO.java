package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AllAndThisMonthEarningsDTO {
    double sumOfAll;
    double sumOfThisMonth;
}
