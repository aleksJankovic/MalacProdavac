package server.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.server.models.OrderStatus;

@Repository
public interface OrderStatusRepository extends CrudRepository<OrderStatus, Long> {
}
