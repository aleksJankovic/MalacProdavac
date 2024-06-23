package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.ChangeOrderStatusRequest;
import server.server.dtos.request.OrderRequest;
import server.server.generalResponses.SuccessResponse;

public interface OrderService {
    ResponseEntity<SuccessResponse> createOrder(String token, OrderRequest orderRequest);
    ResponseEntity<?> getMyCart(String token);
    ResponseEntity<?> getItemsByOrderId(String token, Long orderId);

    ResponseEntity<?> changeOrderStatus(String token, ChangeOrderStatusRequest orderId);
    ResponseEntity<?> getOrderDetailsByOrderId(String token, Long orderId);
}
