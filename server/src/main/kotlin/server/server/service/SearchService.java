package server.server.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SearchService {

    ResponseEntity<?> searchProductsAndSellers(String query) throws IOException;
}
