package server.server.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id",nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id")
    private ShippingMethod shippingMethod;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private PaymentMethod paymentMethod;

    private double longitudeBuyer;
    private double latitudeBuyer;

    private String buyerPhoneNumber;
    
    private String buyerAddress;

    private Date orderDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deliverer_id")
    private Deliverer delivererId;
}
