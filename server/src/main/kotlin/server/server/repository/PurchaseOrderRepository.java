package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import server.server.models.Order;
import server.server.models.Product;
import server.server.models.PurchaseOrder;
import server.server.models.compositeKeys.PurchaseOrderKey;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, PurchaseOrderKey>{

    List<PurchaseOrder> getPurchaseOrderByOrderId(Order order);

    @Query(value = "SELECT * FROM purchase_order WHERE order_id = (:orderId)", nativeQuery = true)
    List<Object[]> getItemsByOrderId(Long orderId);
    //List<PurchaseOrder> getPurchaseOrderByOrderId(Long orderId);

    @Transactional
    Long deleteAllByProduct(Product product);


}
