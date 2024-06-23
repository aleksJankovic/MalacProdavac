package server.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.server.models.DelivererOfferStatus;

@Repository
public interface DelivererOfferStatusRepository extends CrudRepository<DelivererOfferStatus, Long> {
}
