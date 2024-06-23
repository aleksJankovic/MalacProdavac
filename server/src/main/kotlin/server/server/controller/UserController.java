package server.server.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.*;
import server.server.repository.DelivererOffersRepository;
import server.server.service.DelivererService;
import server.server.service.LoginService;
import server.server.service.RegistrationService;
import server.server.service.UserService;
import server.server.validators.constraints.PasswordValidator;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    RegistrationService registrationService;
    @Autowired
    LoginService loginService;
    @Autowired
    UserService userService;
    @Autowired
    private DelivererService delivererService;

    @PostMapping ("/registration/step1")
    public ResponseEntity<?> checkNewUserData(
            @RequestBody UserRegistrationRequest userRegistrationRequest
    ){
        return registrationService.checkNewUserData(userRegistrationRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> userRegistration(
            @RequestBody UserRegistrationRequest userRegistrationRequest
    ){
        return registrationService.createNewUser(userRegistrationRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest userLoginRequest){
        return loginService.loginUser(userLoginRequest.getUsername(), userLoginRequest.getPassword());
    }

    @GetMapping("/personalInformation")
    public ResponseEntity<?> personalInformation(
            @RequestHeader("Authorization") String token
    ){
        return userService.myPersonalInformation(token);
    }

    @GetMapping("/myOrders")
    public ResponseEntity<?> myOrders(@RequestHeader("Authorization") String token){
        return userService.myOrders(token);
    }

    @PostMapping("/updatePersonalInformation")
    public ResponseEntity<?> updatePersonalInformation(@RequestHeader("Authorization") String token,@RequestBody UserUpdateRequest userUpadateRequest){
        return userService.update(token,userUpadateRequest);
    }

    @GetMapping("/lastNews")
    public ResponseEntity<?> getLastNews(@RequestHeader("Authorization") String token){
        return userService.getLastNews(token);
    }

    @GetMapping("/postDetails/{id}")
    public ResponseEntity<?> getMoreInformationAboutPost(@RequestHeader("Authorization") String token, @PathVariable Long id){
        return userService.getMoreInformationAboutPost(token, id);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token, @PathVariable Long id){
        return userService.likePost(token, id);
    }

    @GetMapping("user/graphic/data")
    public ResponseEntity<?> getGraphicDataForBuyer(@RequestHeader("Authorization") String token){
        return userService.getGraphicDataForBuyer(token);
    }

    @PostMapping("follow/{sellerId}")
    public ResponseEntity<?> followSeller(@RequestHeader("Authorization") String token, @PathVariable Long sellerId){
        return userService.followSeller(token,sellerId);
    }

    @GetMapping("updatePersonalInformation/validateOldPassword/{oldPassword}")
    public ResponseEntity<?> validateOldPassword(@RequestHeader("Authorization") String token,
                                                 @PathVariable String oldPassword) {
        return userService.validateOldPassword(token, oldPassword);
    }

    @PostMapping("updatePersonalInformation/changePassword/{newPassword}")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @PathVariable String newPassword) {
        return userService.changePassword(token, newPassword);
    }

    @PostMapping("/profile-image-change")
    public ResponseEntity<?> changeProfilePicture(@RequestHeader("Authorization") String token,
                                                  @RequestBody ChangeImageRequest image){
        return userService.changeProfileImage(token, image);
    }

    @PostMapping("/deliveryRating")
    public ResponseEntity<?> deliveryRating(@RequestHeader("Authorization") String token, @RequestBody DeliveryRatingRequest deliveryRatingRequest){
        return userService.rateDelivery(token, deliveryRatingRequest);
    }

    @PostMapping("/mark-delivered/{orderId}")
    public ResponseEntity<?> markOrderAsDelivered(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long orderId) {
        return userService.markOrderAsDelivered(token, orderId);
    }

    @GetMapping("/get-delivery-offers")
    public ResponseEntity<?> getDeliveryOffers(@RequestHeader("Authorization") String token){
        return delivererService.getDeliveryOffers(token);
    }

    @PostMapping("/change-delivery-offer-status")
    public ResponseEntity<?> changeDeliveryOfferStatus(@RequestHeader("Authorization") String token,
                                                       @RequestParam Long offerId,
                                                       @RequestParam boolean offerAccepted){
        return delivererService.changeDeliveryOfferStatus(token, offerId, offerAccepted);
    }

    @PostMapping("/order-received-confirmation")
    public ResponseEntity<?> confirmOrderReceiving(@RequestHeader("Authorization") String token,
                                                   @RequestParam Long offerId){
        return delivererService.confirmOrderReceiving(token, offerId);
    }

    @GetMapping("/list-off-all-offers")
    public ResponseEntity<?> getAllOffers(@RequestHeader("Authorization") String token){
        return userService.allOffers(token);
    }

    @PostMapping("/set-my-address")
    public ResponseEntity<?> setMyAddress(@RequestHeader("Authorization")String token,@RequestBody ChangeAddressRequest changeAddressRequest){
        return userService.setMyAddress(token,changeAddressRequest);
    }

}
