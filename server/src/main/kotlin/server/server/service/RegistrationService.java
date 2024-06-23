package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.UserRegistrationRequest;
import server.server.dtos.response.EmailUsernameAvailabilityResponse;

public interface RegistrationService {
    ResponseEntity<EmailUsernameAvailabilityResponse>  checkNewUserData(UserRegistrationRequest userRegistrationRequest);
    ResponseEntity<?> createNewUser(UserRegistrationRequest userRegistrationRequest);
}
