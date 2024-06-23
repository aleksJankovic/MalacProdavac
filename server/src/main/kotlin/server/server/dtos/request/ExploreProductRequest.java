package server.server.dtos.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExploreProductRequest {
    List<Long> excludedIds;
}
