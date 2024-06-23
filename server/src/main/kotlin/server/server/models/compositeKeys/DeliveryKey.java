package server.server.models.compositeKeys;

import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class DeliveryKey implements Serializable {
    @Column(name="order_id")
    Long orderId;
    @Column(name="deliverer_id")
    Long delivererId;
}
