package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.Deliverer;
import server.server.models.DeliveryRating;

import java.util.List;

@Repository
public interface DeliveryRatingRepository extends JpaRepository<DeliveryRating, Long> {

    List<DeliveryRating> findAllByDeliverer(Deliverer deliverer);
    @Query(value = "SELECT \n" +
            "  grade, \n" +
            "  COUNT(grade) AS numberOfUsers,\n" +
            "  ROUND(COUNT(grade) * 100.0 / SUM(COUNT(grade)) OVER (), 2) AS percentage\n" +
            "FROM delivery_rating\n" +
            "WHERE deliverer_id = (:delivererId)\n" +
            "GROUP BY grade;", nativeQuery = true)
    List<Object[]> getDeliverersPerformanceAnalyticsGradeNumber(Long delivererId);
}
