package server.server.jwt.Impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.server.jwt.JwtGenerator;
import server.server.models.User;

import java.util.Date;

@Service
public class JwtGeneratorImpl implements JwtGenerator {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expirationInMs}")
    private Long EXPIRATION;
    @Override
    public String generateJwtToken(User user) {
        String jwt = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        return jwt;
    }
}
