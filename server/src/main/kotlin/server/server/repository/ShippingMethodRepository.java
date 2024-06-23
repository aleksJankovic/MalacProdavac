package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.models.ShippingMethod;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
}
