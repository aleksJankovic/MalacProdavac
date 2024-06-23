package server.server.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import server.server.dtos.ExploreProductDTO;

import java.util.List;
@Getter
@Setter
@Builder
public class ExploreProductResponse {
    private List<ExploreProductDTO> randomProducts;
    private List<Long> listOfIDs;
}
