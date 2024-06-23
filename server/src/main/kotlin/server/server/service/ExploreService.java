package server.server.service;

import server.server.dtos.response.ExploreProductResponse;

import java.io.IOException;
import java.util.List;

public interface ExploreService {
    ExploreProductResponse getRandomProducts(List<Long> excludedIds) throws IOException;
}
