package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import server.server.models.Product;
import server.server.models.ProductComment;

import java.util.List;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findByProduct_ProductId(Long productId);

    @Query(value = "SELECT AVG(grade) FROM product_comment WHERE product_id = (:product_id) GROUP BY product_id",
        nativeQuery = true)
    Double getAverageGradeByProductId(Long product_id);
    @Transactional
    Long deleteByProduct(Product product);
}
