package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.server.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryId(Long id);
}
