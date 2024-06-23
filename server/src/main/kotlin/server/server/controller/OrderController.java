package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.OrderRequest;
import server.server.dtos.request.ChangeOrderStatusRequest;
import server.server.generalResponses.SuccessResponse;
import server.server.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping
    public ResponseEntity<SuccessResponse> createOrder( @RequestHeader("Authorization") String token,
                                                        @RequestBody OrderRequest orderRequest){
        return orderService.createOrder(token, orderRequest);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<?> getMyCart(@RequestHeader("Authorization") String token){
        return orderService.getMyCart(token);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItemsByOrderId(@RequestHeader("Authorization") String token,
                                               @RequestParam Long orderId){
        return orderService.getItemsByOrderId(token, orderId);
    }

    @PostMapping("changeOrderStatus")
    public ResponseEntity<?> changeOrderStatus(@RequestHeader("Authorization") String token, @RequestBody ChangeOrderStatusRequest changeOrderStatusRequest){
        return orderService.changeOrderStatus(token, changeOrderStatusRequest);
    }

    @GetMapping("/order-details")
    public ResponseEntity<?> getOrderDetailsByOrderId(@RequestHeader("Authorization") String token,
                                                      @RequestParam Long orderId){
        return orderService.getOrderDetailsByOrderId(token, orderId);
    }

}
