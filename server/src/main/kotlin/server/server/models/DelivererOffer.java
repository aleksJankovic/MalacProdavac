package server.server.models;

import jakarta.persistence.*;
import lombok.*;
import server.server.models.compositeKeys.DeliveryKey;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "deliverer_offers")
public class DelivererOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="deliverer_id")
    private Deliverer deliverer;

    private double price;
    private LocalDateTime dateTime;
    private String comment;
    @OneToOne
    @JoinColumn(name = "offer_status_id")
    private DelivererOfferStatus status;
}
