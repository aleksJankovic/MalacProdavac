package server.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "deliverer_offer_status")
public class DelivererOfferStatus {
    @Id
    @Column(name = "offer_status_id")
    private Long offerStatusId;
    private String name;
}
