package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.server.models.DrivingCategory;

@Repository
public interface DrivingCategoryRepository extends JpaRepository<DrivingCategory, Long> {
    DrivingCategory findByCategoryName(String categoryName);
}
