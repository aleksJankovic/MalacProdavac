package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.ProductCommentRequest;

public interface ProductCommentService {
    ResponseEntity<?> getProductCommentsByProductId(Long productId);

    ResponseEntity<?> postProductComment(String token, ProductCommentRequest productCommentRequest);
}
