package server.server.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import server.server.dtos.request.AddingNewProductRequest;
import server.server.dtos.response.ExploreProductResponse;
import server.server.models.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ResponseEntity<?> getProductById(String token, Long id) throws IOException;

    List<Product> getProductByName(String name);

    @Query("SELECT p.seller FROM Product p WHERE p.productId= :prodcutId")
    ResponseEntity<?> getSellerByProductId(Long productId);

    ResponseEntity<?> addNewProduct(String token, AddingNewProductRequest addingNewProductRequest) throws IOException;

    ResponseEntity<?> getProductsByCategory(Long id);

    ExploreProductResponse getRandomProductsByCategory(List<Long> excludedIds, Long id);
    ResponseEntity<?> changeProductImage(String token, byte[] image, Long productId);
    ResponseEntity<?> deleteProductById(String token, Long productId);
    ResponseEntity<?> editProductById(String token, Long productId, AddingNewProductRequest addingNewProductRequest);
}
