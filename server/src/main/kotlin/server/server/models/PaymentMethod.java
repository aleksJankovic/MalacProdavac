package server.server.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
    @Column(name = "payment_method_name")
    private String paymentMethodName;
}
