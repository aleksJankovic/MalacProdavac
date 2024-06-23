package server.server.service.Impl;

import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.OrderItemDTO;
import server.server.dtos.request.ChangeOrderStatusRequest;
import server.server.dtos.request.OrderItemsRequest;
import server.server.dtos.request.OrderRequest;
import server.server.dtos.request.SellerItemsRequest;
import server.server.dtos.response.InoviceOrderResponse;
import server.server.dtos.response.MyCartResponse;
import server.server.dtos.response.PurchaseOrderItemsResponse;
import server.server.dtos.response.SellerCustomerOrderDetailsAndItemsResponse;
import server.server.emailSystem.EmailDetails;
import server.server.emailSystem.enums.EmailType;
import server.server.emailSystem.service.EmailService;
import server.server.enums.Measurement;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.jwt.exceptions.InvalidTokenException;
import server.server.models.*;
import server.server.models.compositeKeys.PurchaseOrderKey;
import server.server.notifications.NotificationMessage;
import server.server.notifications.service.FirebaseMessagingService;
import server.server.repository.*;
import server.server.service.OrderService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileSystemUtil fileSystem;
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private DelivererOffersRepository delivererOffersRepository;
    @Autowired
    private FirebaseMessagingService firebaseMessagingService;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public ResponseEntity<SuccessResponse> createOrder(String token, OrderRequest orderRequest) {
        User buyer = jwtUtil.isTokenValid(token);

        for (SellerItemsRequest sellerItemRequest : orderRequest.getPurchase()) {
            Seller seller = sellerRepository.findById(sellerItemRequest.getSellerId()).get();

            Order newOrder = Order.builder()
                    .buyer(buyer)
                    .latitudeBuyer(orderRequest.getBuyerLatitude())
                    .longitudeBuyer(orderRequest.getBuyerLongitude())
                    .seller(seller)
                    .orderStatus(orderStatusRepository.findById(1L).get())
                    .shippingMethod(shippingMethodRepository.findById(orderRequest.getShippingMethodId()).get())
                    .paymentMethod(paymentMethodRepository.findById(orderRequest.getPaymentMethodId()).get())
                    .buyerAddress(orderRequest.getBuyerAddress())
                    .buyerPhoneNumber(orderRequest.getPhoneNumber())
                    .orderDate(new Date())
                    .build();
            Order savedOrder = orderRepository.save(newOrder);

            List<OrderItemDTO> orderedItemsList = new ArrayList<>();

            for (OrderItemsRequest item : sellerItemRequest.getOrderItems()) {
                Product product = productRepository.findById(item.getProductId()).get();
                PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                        .purchaseOrderKey(new PurchaseOrderKey(savedOrder.getId(), product.getProductId()))
                        .quantity(item.getQuantity())
                        .build();

                purchaseOrderRepository.save(purchaseOrder);

                orderedItemsList.add(OrderItemDTO.builder()
                        .productName(product.getProductName())
                        .unitPrice(product.getPrice())
                        .quantity(item.getQuantity())
                        .measurement(Measurement.values()[(int) (product.getMeasurement().getMeasurementId() - 1)].name())
                        .build());
            }

            try{
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .recipientToken(notificationRepository.findById(seller.getId()).get().getRecipientToken())
                        .title("Nova narudžbina")
                        .body("Korisnik "+ buyer.getName() +" " + buyer.getSurname() + " je napravio novu porudžbinu.")
                        .image("")
                        .data(null).build();

                firebaseMessagingService.sendNotificationByToken(notificationMessage);
            }
            catch (Exception e){
            }

            InoviceOrderResponse inoviceOrderResponse = InoviceOrderResponse.builder()
                    .orderNumber(savedOrder.getId())
                    .orderDate(savedOrder.getOrderDate())
                    .buyerName(buyer.getName())
                    .buyerSurname(buyer.getSurname())
                    .buyerAddress(orderRequest.getBuyerAddress())
                    .buyerEmail(buyer.getEmail())
                    .buyerPhoneNumber(newOrder.getBuyerPhoneNumber())
                    .sellerName(seller.getUser().getName())
                    .sellerSurname(seller.getUser().getSurname())
                    .sellerAddress(sellerItemRequest.getSellerAddress())
                    .sellerEmail(seller.getUser().getEmail())
                    .sellerAccountNumber(seller.getAccountNumber())
                    .paymentMethodId(savedOrder.getPaymentMethod().getPaymentId())
                    .orderItemsList(orderedItemsList)
                    .build();

            try {
                emailService.sendEmail(EmailDetails.builder()
                        .emailType(EmailType.BUYER)
                        .inoviceOrderResponse(inoviceOrderResponse)
                        .recipient(buyer.getEmail())
                        .subject("Potvrda o kupovini - Porudzbenica broj #" + inoviceOrderResponse.getOrderNumber()).build());

                emailService.sendEmail(EmailDetails.builder()
                        .emailType(EmailType.SELLER)
                        .inoviceOrderResponse(inoviceOrderResponse)
                        .recipient(seller.getUser().getEmail())
                        .subject("Potvrda o prodaji - Porudzbenica broj #" + inoviceOrderResponse.getOrderNumber()).build());

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Obraditit ako nije uspesno dodato u bazu nesto

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Porudzbine uspesno dodate")
                .data(null)
                .build()
                , HttpStatus.OK );
    }

    @Override
    public ResponseEntity<?> getMyCart(String token) {
        User user = jwtUtil.isTokenValid(token);

        List<Object[]> data = orderRepository.getMyCart(user.getUserId());

        List<MyCartResponse> myCart = new ArrayList<>();
        for (Object[] o : data) {
            double totalPrice = (double) o[0];
            Long orderId = (Long) o[1];
            Date date = (Date) o[2];
            Long orderStatus = (Long) o[3];
            Long sellerId = (Long) o[4];

            Seller seller = sellerRepository.findById(sellerId).get();
            String sellerName = seller.getUser().getName() + " " + seller.getUser().getSurname();

            //Parsiranje datuma
            LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
            String orderDate = localDate.getDayOfMonth() + "/" + localDate.getMonth().getValue() + "/" + localDate.getYear();

            MyCartResponse myCartResponse = MyCartResponse.builder()
                    .orderId(orderId)
                    .totalPrice(totalPrice)
                    .orderStatusId(orderStatus)
                    .orderDate(orderDate)
                    .sellerName(sellerName)
                    .sellerPicture(fileSystem.getImageInBytes(String.valueOf(seller.getUser().getUserId()), ImageType.USER))
                    .build();

            myCart.add(myCartResponse);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .data(myCart).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getItemsByOrderId(String token, Long orderId) {
        User user = jwtUtil.isTokenValid(token);

        List<Object[]> purchaseOrderList = purchaseOrderRepository.getItemsByOrderId(orderId);
        List<PurchaseOrderItemsResponse> items = new ArrayList<>();

        for (Object[] o : purchaseOrderList) {
            Long productId = (Long) o[1];
            int quantity = (int) o[2];

            Product product = productRepository.findById(productId).get();

            PurchaseOrderItemsResponse item = PurchaseOrderItemsResponse.builder()
                    .productName(product.getProductName())
                    .productPrice(product.getPrice())
                    .productCategoryId(product.getCategory().getCategoryId())
                    .productImage(fileSystem.getImageInBytes(String.valueOf(productId), ImageType.PRODUCT))
                    .measurementValue(product.getMeasurement_value())
                    .measurement(product.getMeasurement().getName())
                    .quantity(quantity)
                    .productId(product.getProductId())
                    .build();

            items.add(item);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Spisak stavki unutar porudzbenice ciji je id = " + orderId)
                .data(items).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeOrderStatus(String token, ChangeOrderStatusRequest orderStatus) {
        Order order = orderRepository.getOrderById(orderStatus.getOrderId());

        if(orderStatus.getStatusId() == 1){
            order.setOrderStatus(new OrderStatus(1L,"U PRIPREMI"));

            Order newStatus = orderRepository.save(order);

            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("u pripremi!")
                    .build(),HttpStatus.OK);
        }
        else if(orderStatus.getStatusId() == 2){
            order.setOrderStatus(new OrderStatus(2L,"POSLATO"));

            Order newStatus = orderRepository.save(order);

            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("Poslato!")
                    .build(),HttpStatus.OK);
        }
        else if(orderStatus.getStatusId() == 3){
            order.setOrderStatus(new OrderStatus(3L,"DOSTAVLJENO"));
            Order newStatus = orderRepository.save(order);
            List<DelivererOffer> delivererOffer = delivererOffersRepository.getDelivererOfferByOrder_IdAndStatus_OfferStatusId(order.getId(), 2L);

            if(delivererOffer.size() >= 1){
                delivererOffer.get(0).setStatus(new DelivererOfferStatus(4L, "POSILJKA_DOSTAVLJENA"));
                delivererOffersRepository.save(delivererOffer.get(0));
            }
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("narudzbina dostavljena!")
                    .build(),HttpStatus.OK);
        }
        else{
            order.setOrderStatus(new OrderStatus(4L,"U POTRAZI ZA DOSTAVLJACEM"));
            Order newStatus = orderRepository.save(order);

            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("u potrazi za dosatvljacem")
                    .build(),HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getOrderDetailsByOrderId(String token, Long orderId) {
        User user = jwtUtil.isTokenValid(token);

        ResponseEntity<?> listOfOrderItems = getItemsByOrderId(token, orderId);
        List<PurchaseOrderItemsResponse> purchaseOrderItems = (List<PurchaseOrderItemsResponse>) ((SuccessResponse)listOfOrderItems.getBody()).getData();

        Order order = orderRepository.getOrderById(orderId);
        List<DelivererOffer> delivererOffer = delivererOffersRepository.getDelivererOfferByOrder_IdAndStatus_OfferStatusId(orderId, 2L);
        SellerCustomerOrderDetailsAndItemsResponse response = null;
        if(delivererOffer.size() > 0){
            response = SellerCustomerOrderDetailsAndItemsResponse.builder()
                    .orderId(orderId)
                    .buyerName(order.getBuyer().getName())
                    .buyerSurname(order.getBuyer().getSurname())
                    .buyerAddress(order.getBuyerAddress())
                    .buyerLong(order.getLatitudeBuyer())
                    .buyerLat(order.getLongitudeBuyer())
                    .buyerEmail(order.getBuyer().getEmail())
                    .buyerPhoneNumber(order.getBuyerPhoneNumber())
                    .shippingMethodId(order.getShippingMethod().getShippingId())
                    .paymentMethodId(order.getPaymentMethod().getPaymentId())
                    .purchaseItems(purchaseOrderItems)
                    .sellerLong(order.getSeller().getLongitude())
                    .sellerLat(order.getSeller().getLatitude())
                    .comment(delivererOffer.get(0).getComment())
                    .date_time(delivererOffer.get(0).getDateTime().toString())
                    .price(delivererOffer.get(0).getPrice()).build();
        }
        else{
            response = SellerCustomerOrderDetailsAndItemsResponse.builder()
                    .orderId(orderId)
                    .buyerName(order.getBuyer().getName())
                    .buyerSurname(order.getBuyer().getSurname())
                    .buyerAddress(order.getBuyerAddress())
                    .buyerLong(order.getLatitudeBuyer())
                    .buyerLat(order.getLongitudeBuyer())
                    .buyerEmail(order.getBuyer().getEmail())
                    .buyerPhoneNumber(order.getBuyerPhoneNumber())
                    .shippingMethodId(order.getShippingMethod().getShippingId())
                    .paymentMethodId(order.getPaymentMethod().getPaymentId())
                    .purchaseItems(purchaseOrderItems)
                    .sellerLong(order.getSeller().getLongitude())
                    .sellerLat(order.getSeller().getLatitude()).build();
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .data(response).build(), HttpStatus.OK);
    }
}
