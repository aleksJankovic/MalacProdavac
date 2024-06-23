package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.service.SearchService;

import java.io.IOException;

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    SearchService searchService;
    @GetMapping("/search/{query}")
    public ResponseEntity<?> searchProductsAndSellers(@PathVariable String query) throws IOException {
        return searchService.searchProductsAndSellers(query);
    }

}
