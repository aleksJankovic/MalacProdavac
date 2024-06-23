package server.server.jwt.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.server.exceptions.AccessDeniedException;
import server.server.jwt.JwtUtil;
import server.server.jwt.exceptions.InvalidTokenException;
import server.server.models.User;
import server.server.repository.UserRepository;

@Service
public class JwtUtilImpl implements JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expirationInMs}")
    private Long EXPIRATION;

    @Autowired
    private UserRepository userRepository;

    @SneakyThrows
    @Override
    public Claims decode(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @SneakyThrows
    @Override
    public User isTokenValid(String token){
        if(token == null || token.isEmpty())
            throw new InvalidTokenException("THE TOKEN IS MISSING");

        Claims claims = decode(token);
        User user = userRepository.findByUsernameCustom((String) claims.get("sub"));

        if(user == null)
            throw new InvalidTokenException("THE TOKEN IS INVALID");

        return user;
    }


}
