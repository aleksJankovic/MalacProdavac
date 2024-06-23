package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.ChangeImageRequest;
import server.server.dtos.request.ChangeAddressRequest;
import server.server.dtos.request.ChangePasswordRequest;
import server.server.dtos.request.DeliveryRatingRequest;
import server.server.dtos.request.UserUpdateRequest;

public interface UserService {
    ResponseEntity<?> myPersonalInformation(String token);
    ResponseEntity<?> myOrders(String token);

    ResponseEntity<?> update(String token, UserUpdateRequest userUpdateRequest);

    ResponseEntity<?> getLastNews(String token);

    ResponseEntity<?> getMoreInformationAboutPost(String token, Long postId);

    ResponseEntity<?> likePost(String token, Long id);

    ResponseEntity<?> getGraphicDataForBuyer(String token);

    ResponseEntity<?> followSeller(String token, Long id);

   // ResponseEntity<?> changePassword(String token, ChangePasswordRequest passwordRequest);

    ResponseEntity<?> validateOldPassword(String token, String oldPassword);

    ResponseEntity<?> changePassword(String token, String newPassword);
    ResponseEntity<?> changeProfileImage(String token, ChangeImageRequest image);

    ResponseEntity<?> rateDelivery(String token, DeliveryRatingRequest deliveryRatingRequest);

    ResponseEntity<?> markOrderAsDelivered(String token, Long orderId);

    ResponseEntity<?> allOffers(String token);

    ResponseEntity<?> setMyAddress(String token, ChangeAddressRequest changeAddressRequest);
}
