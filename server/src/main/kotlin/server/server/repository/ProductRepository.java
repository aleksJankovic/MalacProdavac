package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.Category;
import server.server.models.Product;
import server.server.models.Seller;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long productId);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    @Query("SELECT p.seller FROM Product p WHERE p.productId = :productId")
    Seller findSellerByProductId(Long productId);

    @Query("SELECT p FROM Product p WHERE " +
            "p.productName LIKE CONCAT('%',:query, '%')")
    List<Product> searchProduct(String query);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> getProductsBySellerAndQuery(String query, Long sellerId);

    @Query(value = "SELECT * FROM products WHERE product_id NOT IN (:excludedIds) ORDER BY rand() LIMIT 48", nativeQuery = true)
    List<Product> getRandom48Products(List<Long> excludedIds);

    @Query(value = "SELECT * FROM products WHERE categeory = (:category) AND" +
            " product_id NOT IN (:excludedIds) ORDER BY rand() LIMIT 9", nativeQuery = true)
    List<Product> getRandom9ProductsByCategory(List<Long> excludedIds, Category category);

    @Query(value = "SELECT count(*) FROM products WHERE seller_id = (:sellerId)", nativeQuery = true)
    Long getNumberOfProductsBySellerId(Long sellerId);
    
    Product getByProductId(Long productID);

    List<Product> getProductsByCategory(Category category);

    List<Product> findAllBySeller(Seller seller);

    List<Product> findAllBySellerAndCategoryCategoryId(Seller seller, Long categoryId);

}
