package server.server.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "shipping_methods")
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long shippingId;
    @Column(name = "shipping_method_name")
    private String shippingMethodName;
}
