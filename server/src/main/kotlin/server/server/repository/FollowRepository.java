package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.Follow;
import server.server.models.Seller;
import server.server.models.User;
import server.server.models.compositeKeys.FollowKey;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowKey> {

    List<Follow> getByUser_UserId(Long id);
    @Query(value = "SELECT count(*) FROM follow WHERE seller_id = (:seller_id)", nativeQuery = true)
    Long getNumberOfFollowersBySellerId(Long seller_id);

    List<Follow> findAllBySeller_Id(Long id);

}
