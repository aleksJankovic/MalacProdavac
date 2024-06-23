package server.server.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsGradePercentageResponse {
    private Long grade;
    private Double percentage;
}
