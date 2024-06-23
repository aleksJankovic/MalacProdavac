package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.server.models.Seller;
import server.server.models.WorkingTime;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, Long> {

    WorkingTime findBySeller(Seller seller);
}
