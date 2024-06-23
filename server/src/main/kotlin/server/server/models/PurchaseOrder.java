package server.server.models;

import jakarta.persistence.*;
import lombok.*;
import server.server.models.compositeKeys.PurchaseOrderKey;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @EmbeddedId
    private PurchaseOrderKey purchaseOrderKey;

    @ManyToOne
    @JoinColumn(name="order_id",insertable = false, updatable = false)
    private Order orderId;

    @ManyToOne
    @JoinColumn(name="product_id",insertable = false, updatable = false)
    private Product product;

    private int quantity;
}
