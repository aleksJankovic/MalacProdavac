package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.Seller;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
        Seller findByPib(String pib);

       @Query("SELECT s FROM Seller s WHERE " +
               "s.user.name LIKE CONCAT('%',:query, '%')"+
               "Or s.user.username LIKE CONCAT('%', :query, '%')")
       List<Seller> searchSellers(String query);

       Seller findSellerByUserUserId(Long id);

       Seller getSellerById(Long id);

}
