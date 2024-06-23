package server.server.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "deliverers")
public class Deliverer {
    @Id
    private Long id;
    @OneToOne (cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name ="deliverer_id")
    private User user;

    private String location;
    private double longitude;
    private double latitude;
}
