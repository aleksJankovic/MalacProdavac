package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.AddingNewProductRequest;
import server.server.dtos.request.ExploreProductRequest;
import server.server.dtos.response.ExploreProductResponse;
import server.server.service.ProductService;

import java.io.IOException;


@RestController
@RequestMapping
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProduct/{id}")
    public ResponseEntity<?> getProductById(@RequestHeader(value = "Authorization", required = false) String token,@PathVariable Long id) throws IOException {
        return productService.getProductById(token,id);

    }

    @GetMapping("/getSellerByProductId/{id}")
    public ResponseEntity<?> getSellerByProductId(@PathVariable Long id){
       return productService.getSellerByProductId(id);
    }

    @PostMapping("/addNewProduct")
    public ResponseEntity<?> addNewProduct(@RequestHeader("Authorization") String token,
                                            @RequestBody AddingNewProductRequest addingNewProductRequest) throws IOException {
        return productService.addNewProduct(token,addingNewProductRequest);
    }

    @GetMapping("/getProductsByCategory/{id}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable Long id){
        return productService.getProductsByCategory(id);
    }

    @GetMapping("/getRandomProductsByCategory/{id}")
    public ExploreProductResponse getRandomProductsByCategory(@RequestBody ExploreProductRequest exploreProductRequest, @PathVariable Long id){
        return productService.getRandomProductsByCategory(exploreProductRequest.getExcludedIds(),id);
    }

    @PostMapping("/product-change-image")
    public ResponseEntity<?> changeProductImage(@RequestHeader("Authorization") String token,
                                                @RequestBody byte[] image,
                                                @RequestParam Long productId){
        return productService.changeProductImage(token, image, productId);
    }

    @DeleteMapping("/delete-product")
    public ResponseEntity<?> deleteProductById(@RequestHeader("Authorization") String token,
                                               @RequestParam Long productId){
        return productService.deleteProductById(token, productId);
    }

    @PostMapping("/edit-product")
    public ResponseEntity<?> editProductById(@RequestHeader("Authorization") String token,
                                         @RequestParam Long productId,
                                         @RequestBody AddingNewProductRequest addingNewProductRequest){
        return productService.editProductById(token, productId, addingNewProductRequest);
    }
}
