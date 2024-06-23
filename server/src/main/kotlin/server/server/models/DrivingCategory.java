package server.server.models;

import jakarta.persistence.*;

@Entity
@Table(name = "driving_categories")
public class DrivingCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driving_category_id")
    private Long drivingCategoryId;

    @Column(name = "category_name")
    private String categoryName;

    public DrivingCategory() {
    }

    public DrivingCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public DrivingCategory(Long drivingCategoryId, String categoryName) {
        this.drivingCategoryId = drivingCategoryId;
        this.categoryName = categoryName;
    }



    public Long getDrivingCategoryId() {
        return drivingCategoryId;
    }

    public void setDrivingCategoryId(Long drivingCategoryId) {
        this.drivingCategoryId = drivingCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "DrivingCategory{" +
                "drivingCategoryId=" + drivingCategoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
