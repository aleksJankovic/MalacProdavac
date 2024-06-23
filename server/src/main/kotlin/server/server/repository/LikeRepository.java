package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.server.models.Like;
import server.server.models.Post;
import server.server.models.compositeKeys.LikeKey;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeKey> {
    List<Like> findAllByPost(Post post);
}
