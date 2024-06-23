package server.server.service;

import org.springframework.http.ResponseEntity;

public interface LoginService {
    ResponseEntity<?> loginUser(String username, String password);
}
