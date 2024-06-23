package server.server.models;

import jakarta.persistence.*;

@Entity
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    private Long orderStatusId;

    private String name;

    public OrderStatus() {
    }

    public OrderStatus(String name) {
        this.name = name;
    }

    public OrderStatus(Long orderStatusId, String name) {
        this.orderStatusId = orderStatusId;
        this.name = name;
    }

    public Long getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Long orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "orderStatusId=" + orderStatusId +
                ", name='" + name + '\'' +
                '}';
    }
}
