package server.server.models.compositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DriverLicensesKey implements Serializable {
    @Column(name="user_id")
    private Long userId;
    @Column(name="driving_category_id")
    private Long drivingCategoryId;
}
