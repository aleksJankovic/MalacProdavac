package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users WHERE BINARY username = (:username)", nativeQuery = true)
    User findByUsernameCustom(String username);
    User findByEmail(String email);
    User findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);



}
