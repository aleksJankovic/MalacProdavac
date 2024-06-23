package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.ChangeAddressRequest;
import server.server.dtos.request.WorkingTimeRequest;
import server.server.service.OrderService;
import server.server.service.SellerService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class SellerController {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private OrderService orderService;
    @GetMapping("/getSeller/{id}")
    public ResponseEntity<?> getHouseholdById(@PathVariable Long id){
        return sellerService.getHouseholdById(id);
    }

    @GetMapping("seller/graphic/data")
    public ResponseEntity<?> getGraphicDataForSeller(@RequestHeader("Authorization")  String token){
        return sellerService.getGraphicDataForSeller(token);
    }

    @PostMapping("addPost/{text}")
    public ResponseEntity<?> addNewPost(@RequestHeader("Authorization") String token, @PathVariable String text){
        return sellerService.addNewPost(token, text);
    }

    @GetMapping("seller/allProducts")
    public ResponseEntity<?> getAllProducts(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam(value = "id", required = false) Long id){
        return sellerService.getAllProducts(token, id);
    }

    @GetMapping("seller/allProducts/{categoryId}")
    public ResponseEntity<?> getAllProductsByCategoryId(@RequestHeader("Authorization") String token, @PathVariable Long categoryId){
        return sellerService.getAllProductsByCategoryId(token,categoryId);
    }
    @GetMapping("seller/personalInformation")
    public ResponseEntity<?> getPersonalInformation(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "id", required = false) Long idSeller
    ) {
        return sellerService.getMyPersonalInformation(token, idSeller);
    }

    @GetMapping("seller/allPosts")
    public ResponseEntity<?> getAllPosts(@RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam(value = "id", required = false) Long id){
        return sellerService.getMyPosts(token, id);
    }

    @GetMapping("seller/allPosts/{postId}")
    public ResponseEntity<?> getPostDetails(@RequestHeader("Authorization") String token, @PathVariable Long postId){
        return sellerService.getPostDetails(token, postId);
    }

    @GetMapping("/getAllSellers")
    public ResponseEntity<?> getAllSellers(){
        return sellerService.getAllSellers();
    }

    @GetMapping("/seller/searchMyProducts/{query}")
    public ResponseEntity<?> searchMyProducts(@RequestHeader("Authorization") String token,@PathVariable String query){
        return sellerService.searchMyProducts(token, query);
    }

    @GetMapping("/searchByDate/{date}")
    public ResponseEntity<?> searchPostsByDate(@RequestHeader("Authorization") String token,@PathVariable @DateTimeFormat(pattern = "yyyy--MM-dd") LocalDate date) {
        return sellerService.searchPostsByDate(token,date);
    }

    @PostMapping("/changeAddress")
    public ResponseEntity<?> changeAddress(@RequestHeader("Authorization") String token, @RequestBody ChangeAddressRequest changeAddressRequest){
        return sellerService.changeAddress(token, changeAddressRequest);
    }

    @PostMapping("/changeAvailability/{productId}")
    public ResponseEntity<?> changeAvailabilityOfProduct(@RequestHeader("Authorization") String token, @PathVariable Long productId){
        return sellerService.changeAvailabilityOfProduct(token, productId);
    }

    @GetMapping("/allOrders")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String token){
        return sellerService.getAllOrders(token);
    }

    @PostMapping("/setWorkingTime")
    public ResponseEntity<?> setWorkingTime(@RequestHeader(value = "Authorization", required = false) String token,@RequestBody WorkingTimeRequest workingTimeRequest){
        return sellerService.setWorkingTime(token, workingTimeRequest);
    }

    @GetMapping("/getWorkingTime")
    public ResponseEntity<?> getWorkingTime(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (value = "id", required = false) Long id){
        return sellerService.getWorkingTime(token, id);
    }

    //Sve porudzbine kupaca od ovog prodavca
    @GetMapping("seller/get-all-customer-orders")
    public ResponseEntity<?> getAllCustomerOrders(@RequestHeader("Authorization") String token){
        return sellerService.getAllCustomerOrders(token);
    }
}
