package server.server.models;

import jakarta.persistence.*;
import lombok.*;
import server.server.models.compositeKeys.DriverLicensesKey;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriversLicenses {
   @EmbeddedId
    private DriverLicensesKey driverLicensesKey;

    @ManyToOne
    @JoinColumn(name="user_id",insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="driving_category_id",insertable = false, updatable = false)
    private DrivingCategory drivingCategory;

}
