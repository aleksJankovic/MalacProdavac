package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.ProductCommentRequest;
import server.server.service.ProductCommentService;

@RestController
@RequestMapping
public class ProductCommentController {

    @Autowired
    ProductCommentService productCommentService;

    @GetMapping("/getComments/{productId}")
    public  ResponseEntity<?> getAllCommentsForProduct(@PathVariable Long productId){
        return productCommentService.getProductCommentsByProductId(productId);
    }

    @PostMapping("/productComment")
    public ResponseEntity<?> postCommentForProduct(@RequestHeader("Authorization") String token, @RequestBody ProductCommentRequest productCommentRequest){
        return productCommentService.postProductComment(token,productCommentRequest);
    }

}
