package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.server.models.Deliverer;
import server.server.models.User;

@Repository
public interface DelivererRepository extends JpaRepository<Deliverer, Long>{
    Deliverer getByUser(User user);

    Deliverer getById(Long delivererId);
}
