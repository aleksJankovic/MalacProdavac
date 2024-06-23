package server.server.models.compositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PurchaseOrderKey implements Serializable {
    @Column(name = "order_id")
    private Long order_id;
    @Column(name = "product_id")
    private Long product_id;
}
