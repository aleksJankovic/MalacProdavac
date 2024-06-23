package server.server.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.request.TipForJobRequest;
import server.server.dtos.request.WorkingTimeRequest;
import server.server.dtos.request.simulator.AddingNewProductRequestSimulator;
import server.server.dtos.request.simulator.SimulatorOrderItemsRequest;
import server.server.dtos.request.simulator.SimulatorOrderRequest;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.SuccessResponse;
import server.server.models.*;
import server.server.models.compositeKeys.LikeKey;
import server.server.models.compositeKeys.PurchaseOrderKey;
import server.server.repository.*;
import server.server.service.SimulatorService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class SimulatorServiceImpl implements SimulatorService {
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ProductCommentRepository productCommentRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private FileSystemUtil fileSystem;
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private WorkingTimeRepository workingTimeRepository;
    @Autowired
    private DelivererOffersRepository delivererOffersRepository;
    @Autowired
    private DelivererRepository delivererRepository;
    @Autowired
    private DelivererOfferStatusRepository delivererOfferStatusRepository;
    @Override
    public ResponseEntity<?> addNewProduct(AddingNewProductRequestSimulator addingNewProductRequest) {
        Product newProduct = Product.builder()
                .productName(addingNewProductRequest.getProduct_name())
                .category(categoryRepository.findById((long)addingNewProductRequest.getCategory_id()).get())
                .seller(sellerRepository.findById(addingNewProductRequest.getSeller_id()).get())
                .measurement(measurementRepository.findByMeasurementId((long)(addingNewProductRequest.getMeasurement_id())).get())
                .price(addingNewProductRequest.getPrice())
                .description(addingNewProductRequest.getDescription())
                .available(true)
                .build();

        if(addingNewProductRequest.getMeasurement_value() != null)
            newProduct.setMeasurement_value(addingNewProductRequest.getMeasurement_value());

        Product savedProduct = productRepository.save(newProduct);

        fileSystem.saveImage(String.valueOf(savedProduct.getProductId()), addingNewProductRequest.getPicture(), ImageType.PRODUCT);

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Product added successfully.")
                .data(savedProduct.getProductId()).build()
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createAnOrder(SimulatorOrderRequest simulatorOrderRequest) {
        Order order = Order.builder()
                .buyer(userRepository.findById(simulatorOrderRequest.getBuyerId()).get())
                .latitudeBuyer(simulatorOrderRequest.getBuyerLatitude())
                .longitudeBuyer(simulatorOrderRequest.getBuyerLongitude())
                .orderDate(generateRandomOrderDate())
                .orderStatus(orderStatusRepository.findById(simulatorOrderRequest.getOrderStatusId()).get())
                .paymentMethod(paymentMethodRepository.findById(simulatorOrderRequest.getPaymentMethodId()).get())
                .shippingMethod(shippingMethodRepository.findById(simulatorOrderRequest.getShippingMethodId()).get())
                .buyerPhoneNumber(simulatorOrderRequest.getPhoneNumber())
                .buyerAddress(simulatorOrderRequest.getAddress())
                .seller(sellerRepository.getSellerById(simulatorOrderRequest.getSellerId()))
                .build();

        Order savedOrder = orderRepository.save(order);
        if(savedOrder == null)
            return new ResponseEntity<>("[NEUSPESNO]: Porudzbina nije uspesno zabelezena.", HttpStatus.INTERNAL_SERVER_ERROR);

        for(SimulatorOrderItemsRequest item : simulatorOrderRequest.getPurchase()){
            PurchaseOrder purchase = PurchaseOrder.builder()
                    .purchaseOrderKey(new PurchaseOrderKey(savedOrder.getId(), item.getProductId()))
                    .quantity(item.getQuantity())
                    .build();

            purchaseOrderRepository.save(purchase);
        }

        return new ResponseEntity<>("[USPESNO]: Porudzbina je uspesno zabelezena!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createAPost(Long sellerId, String text) {
        Post post = Post.builder()
                .seller(sellerRepository.getSellerById(sellerId))
                .text(text)
                .dateTime(generateRandomOrderLocalDateTime()).build();

        Post newPost = postRepository.save(post);
        return newPost == null ? new ResponseEntity<>("[NEUSPESNO]: Objava nije kreirana!", HttpStatus.INTERNAL_SERVER_ERROR) : new ResponseEntity<>(newPost.getPostId(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> likePost(Long postId, Long userId) {
        Like like = Like.builder()
                .likeKey(new LikeKey(postId, userId))
                .dateTime(generateRandomOrderLocalDateTime())
                .build();

        Like savedLike = likeRepository.save(like);
        return savedLike == null ? new ResponseEntity<>("[NEUSPESNO]: Lajk nije sacuvan!", HttpStatus.INTERNAL_SERVER_ERROR) : new ResponseEntity<>("[USPESNO]: Lajk sacuvan!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> commentProduct(Long productId, Long userId, int grade, String comment) {
        ProductComment productComment = ProductComment.builder()
                .product(productRepository.getByProductId(productId))
                .user(userRepository.findById(userId).get())
                .grade(grade)
                .text(comment)
                .date(generateRandomOrderDate())
                .build();

        ProductComment savedComment = productCommentRepository.save(productComment);

        return savedComment == null ? new ResponseEntity<>("[NEUSPESNO]: Komentar za proizvod nije sacuvan!", HttpStatus.INTERNAL_SERVER_ERROR) : new ResponseEntity<>("[USPESNO]: Uspesno komentarisanje proizvoda!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> commentPost(Long postId, Long userId, String comment) {
        PostComment postComment = PostComment.builder()
                .post(postRepository.getPostByPostId(postId))
                .user(userRepository.findById(userId).get())
                .text(comment)
                .dateTime(generateRandomOrderLocalDateTime())
                .build();

        PostComment newPostComment = postCommentRepository.save(postComment);
        return newPostComment == null ? new ResponseEntity<>("[NEUSPESNO]: Komentar za objavu nije sacuvan!", HttpStatus.INTERNAL_SERVER_ERROR) : new ResponseEntity<>("[USPESNO]: Uspesno komentarisanje objave!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> simulateSellersWorkingTime(Long sellerId, WorkingTimeRequest workingTimeRequest){
        User user = userRepository.findById(sellerId).get();
        Seller seller = sellerRepository.getSellerById(user.getUserId());

        WorkingTime wt = workingTimeRepository.findBySeller(seller);
        if(wt == null){
            WorkingTime workingTime = new WorkingTime();
            workingTime.setSeller(seller);
            workingTime.setMonday(workingTimeRequest.getMonday());
            workingTime.setTuesday(workingTimeRequest.getTuesday());
            workingTime.setWednesday(workingTimeRequest.getWednesday());
            workingTime.setThursday(workingTimeRequest.getThursday());
            workingTime.setFriday(workingTimeRequest.getFriday());
            workingTime.setSaturday(workingTimeRequest.getSaturday());
            workingTime.setSunday(workingTimeRequest.getSunday());
            workingTimeRepository.save(workingTime);
        }
        else{
            wt.setMonday(workingTimeRequest.getMonday());
            wt.setTuesday(workingTimeRequest.getTuesday());
            wt.setWednesday(workingTimeRequest.getWednesday());
            wt.setThursday(workingTimeRequest.getThursday());
            wt.setFriday(workingTimeRequest.getFriday());
            wt.setSaturday(workingTimeRequest.getSaturday());
            wt.setSunday(workingTimeRequest.getSunday());
            workingTimeRepository.save(wt);
        }

        return new ResponseEntity<>("[USPESNO]: Radno vreme prodavca uspesno setovano", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> sendTipForJob(Long delivererId, TipForJobRequest tipForJobRequest) {
        User user = userRepository.findById(delivererId).get();
        Deliverer deliverer = delivererRepository.getByUser(user);
        Order order = orderRepository.getOrderById(tipForJobRequest.getOrderId());

        DelivererOffer delivererOffer = DelivererOffer.builder()
                .deliverer(deliverer)
                .order(order)
                .comment(tipForJobRequest.getComment())
                .price(tipForJobRequest.getPrice())
                .dateTime(generateRandomOrderLocalDateTime())
                .status(delivererOfferStatusRepository.findById(1L).get())
                .build();

        delivererOffersRepository.save(delivererOffer);

        return new ResponseEntity<>("Uspesno kreirana ponuda za posao.", HttpStatus.OK);
    }

    private Date generateRandomOrderDate(){
        Random random = new Random();
        int randomNumber = random.nextInt(21) + 1;

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_YEAR, -randomNumber);
        return calendar.getTime();
    }

    private LocalDateTime generateRandomOrderLocalDateTime() {
        Random random = new Random();
        int randomNumber = random.nextInt(21) + 1;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime randomOrderDate = now.minus(randomNumber, ChronoUnit.DAYS);

        return randomOrderDate;
    }
}
