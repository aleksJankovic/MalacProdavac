package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import server.server.models.Order;
import server.server.models.OrderStatus;
import server.server.models.Seller;
import server.server.models.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT seller_id AS seller_id, count(*) AS number_of_orders FROM orders WHERE MONTH(order_date) = MONTH(NOW()) GROUP BY(seller_id) ORDER BY count(*) DESC LIMIT 3;",
            nativeQuery = true)
    List<Object[]> getTop3SellersOfTheMonth();

    @Query(value = "SELECT product_id FROM purchase_order WHERE order_id IN " +
            "(SELECT order_id FROM orders WHERE month(order_date) = month(NOW())) GROUP BY product_id ORDER BY count(*) DESC LIMIT 3",
                nativeQuery = true)
    List<Long> getTop3ProductsOfTheMonth();


    List<Order> getOrdersByBuyer_UserId(Long buyer_id);

    List<Order> getOrdersBySeller(Seller seller);

    List<Order> findAll();

    Order getOrderById(Long id);

    @Query(value = "SELECT COUNT(seller_id) AS numberOfOrders, DATE(order_date) as orderDate FROM orders WHERE seller_id = (:sellerId) AND DATE(order_date) BETWEEN (:currentWeekStartDate) AND (:currentWeekEndDate) GROUP BY DATE(order_date) ORDER BY DATE(order_date) ASC;",
            nativeQuery = true)
    List<Object[]> getGraphicDataForSeller(Long sellerId,
                                       LocalDate currentWeekStartDate, LocalDate currentWeekEndDate);


    @Query(value = "SELECT \n" +
            "    p.category_id, \n" +
            "    sum(po.quantity)\n" +
            "FROM \n" +
            "    products p\n" +
            "JOIN \n" +
            "    purchase_order po ON p.product_id = po.product_id\n" +
            "WHERE \n" +
            "    EXISTS (\n" +
            "        SELECT 1\n" +
            "        FROM orders o\n" +
            "        WHERE \n" +
            "            o.buyer_id = (:buyerId) \n" +
            "            AND o.order_id = po.order_id\n" +
            "    )\n" +
            "GROUP BY p.category_id", nativeQuery = true)
    List<Object[]> getGraphicDataForBuyer(Long buyerId);

    @Query(value = "SELECT sum(po.totalPrice) as totalPrice, po.orderId, order_details.orderDate, order_details.orderStatusId, order_details.sellerId\n" +
            "FROM (SELECT po.order_id as orderId, po.product_id as productId, po.quantity * p.price as totalPrice\n" +
            "FROM purchase_order po\n" +
            "LEFT JOIN products as p ON po.product_id = p.product_id) as po\n" +
            "JOIN (SELECT order_id as orderId, DATE(order_date) as orderDate, order_status_id as orderStatusId, seller_id as sellerId\n" +
            "\t\t\tFROM orders\n" +
            "\t\t\tWHERE buyer_id = (:buyerId)) as order_details ON po.orderId = order_details.orderId\n" +
            "GROUP BY po.orderId, order_details.orderDate, order_details.orderStatusId, order_details.sellerId\n" +
            "ORDER BY order_details.orderDate DESC", nativeQuery = true)
    List<Object[]> getMyCart(Long buyerId);

    List<Order> getOrderBySellerOrderByOrderDateDesc(Seller seller);
    List<Order> getOrdersByOrderStatusOrderByOrderDateDesc(OrderStatus orderStatus);
}
