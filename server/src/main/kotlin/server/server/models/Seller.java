package server.server.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sellers")
public class Seller {
    @Id
    private Long id;

    @OneToOne (cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name ="seller_id")
    private User user;


    private String pib;
    private String address;

    private double longitude;
    private double latitude;

    private String accountNumber;
}
