package server.server.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import server.server.dtos.response.LoginFailedResponse;
import server.server.exceptions.InvalidLoginCredentialsException;
import server.server.jwt.JwtGenerator;
import server.server.models.User;
import server.server.repository.UserRepository;
import server.server.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtGenerator jwtGenerator;
    @Override
    public ResponseEntity<?> loginUser(String username, String password) {
        User user = userRepository.findByUsernameCustom(username);
        if(user == null)
            throw new InvalidLoginCredentialsException("Invalid login credentials.", new LoginFailedResponse(true, false));

        if(!BCrypt.checkpw(password, user.getPassword()))
            throw new InvalidLoginCredentialsException("Invalid login credentials.", new LoginFailedResponse(false, true));

        //Generisati jwt token
        return new ResponseEntity<>(jwtGenerator.generateJwtToken(user), HttpStatus.OK);
    }
}
