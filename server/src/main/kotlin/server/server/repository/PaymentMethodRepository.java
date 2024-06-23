package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.models.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
