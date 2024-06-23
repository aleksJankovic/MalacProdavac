package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.ExploreProductRequest;
import server.server.dtos.response.ExploreProductResponse;
import server.server.service.ExploreService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/explore")
public class ExploreController {
    private static int PAGE_SIZE = 48;
    @Autowired
    ExploreService exploreService;
    //id, naziv, slika
    @PostMapping("/random/products")
    public ExploreProductResponse getPageOfRandomProducts(@RequestBody ExploreProductRequest exploreProductRequest) throws IOException {
        return exploreService.getRandomProducts(exploreProductRequest.getExcludedIds());
    }
}
