package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.models.Notification;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(Long productId);
}
