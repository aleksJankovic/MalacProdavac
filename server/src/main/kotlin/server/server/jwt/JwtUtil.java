package server.server.jwt;

import io.jsonwebtoken.Claims;
import server.server.models.User;

public interface JwtUtil {
    Claims decode(String token);
    User isTokenValid(String token);
}
