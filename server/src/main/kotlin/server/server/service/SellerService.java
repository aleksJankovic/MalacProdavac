package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.ChangeAddressRequest;
import server.server.dtos.request.WorkingTimeRequest;

import java.time.LocalDate;

public interface SellerService {
    ResponseEntity<?> getHouseholdById(Long id);
    ResponseEntity<?> getGraphicDataForSeller(String token);

    ResponseEntity addNewPost(String token, String postText);

    ResponseEntity<?> getAllProducts(String token, Long id);

    ResponseEntity<?> getMyPersonalInformation(String token, Long idSeller);

    ResponseEntity<?> getAllProductsByCategoryId(String token, Long categoryId);

    ResponseEntity<?> getMyPosts(String token, Long idSeller);

    ResponseEntity<?> getPostDetails(String token, Long postId);

    ResponseEntity<?> getAllSellers();

    ResponseEntity<?> searchMyProducts(String token, String query);

    ResponseEntity<?> searchPostsByDate(String token,LocalDate date);

    ResponseEntity<?> changeAddress(String token, ChangeAddressRequest changeAddressRequest);

    ResponseEntity<?> changeAvailabilityOfProduct(String token, Long productId);

    ResponseEntity<?> getAllOrders(String token);

    ResponseEntity<?> setWorkingTime(String token, WorkingTimeRequest workingTimeRequest);

    ResponseEntity<?> getWorkingTime(String token, Long id);
    ResponseEntity<?> getAllCustomerOrders(String token);
}
