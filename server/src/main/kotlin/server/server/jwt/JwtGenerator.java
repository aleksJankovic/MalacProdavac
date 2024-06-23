package server.server.jwt;

import server.server.models.User;

public interface JwtGenerator {
    String generateJwtToken(User user);
}
