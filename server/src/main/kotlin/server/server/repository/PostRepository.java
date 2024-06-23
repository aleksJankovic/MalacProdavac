package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.server.models.Post;
import server.server.models.Seller;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> getPostBySeller(Seller seller);

    Post getPostByPostId(Long id);

    List<Post> findBySellerAndDateTimeBetween(Seller seller, LocalDateTime startOfDay, LocalDateTime endOfDay);
}

