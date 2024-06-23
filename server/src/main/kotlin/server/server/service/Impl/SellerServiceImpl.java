package server.server.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.*;
import server.server.dtos.request.ChangeAddressRequest;
import server.server.dtos.request.WorkingTimeRequest;
import server.server.dtos.response.SellerCustomerOrderResponse;
import server.server.dtos.response.SellerWeeklyNumberOfOrders;
import server.server.exceptions.AccessDeniedException;
import server.server.exceptions.InvalidSellerIdException;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.ErrorResponse;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.*;
import server.server.models.compositeKeys.FollowKey;
import server.server.repository.*;
import server.server.service.SellerService;
import server.server.utils.FormatDateCustomUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private FileSystemUtil fileSystem;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    FileSystemUtil fileSystemUtil;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private ProductCommentRepository productCommentRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private WorkingTimeRepository workingTimeRepository;

    @SneakyThrows
    @Override
    public ResponseEntity<?> getHouseholdById(Long sellerId) {
        Seller seller = sellerRepository.getSellerById(sellerId);

        if (seller == null) {
            throw new InvalidSellerIdException("Proizcodjac sa ovim id-om ne postoji!");
        }

        List<Follow> follows = followRepository.findAllBySeller_Id(seller.getId());
        List<Post> posts = postRepository.getPostBySeller(seller);

        double sum = 0.0;
        double br = 0;
        List<Product> products = productRepository.findAllBySeller(seller);
        for (Product p:products) {
            List<ProductComment> productComments = productCommentRepository.findByProduct_ProductId(p.getProductId());
            for (ProductComment pc: productComments) {
                sum += pc.getGrade();
                br++;
            }
        }

        double avg;
        if(br>0) avg = sum/br;
        else avg = -1;

        return new ResponseEntity<>(SellerDTO.builder()
                .seller_id(sellerId)
                .name(seller.getUser().getName())
                .username(seller.getUser().getUsername())
                .surname(seller.getUser().getSurname())
                .email(seller.getUser().getEmail())
                .picture(fileSystem.getImageInBytes(String.valueOf(seller.getUser().getUserId()), ImageType.USER))
                .pib(seller.getPib())
                .adress(seller.getAddress())
                .latitude(seller.getLatitude())
                .longitude(seller.getLongitude())
                .numberOfFollowers(follows.size())
                .numberOfPosts(posts.size())
                .avgGrade(avg).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getGraphicDataForSeller(String token) {
        User seller = jwtUtil.isTokenValid(token);

        LocalDate date = LocalDate.now();
        DayOfWeek dayOfTheWeek = date.getDayOfWeek();

        LocalDate currentWeekStartDate = date.minusDays(dayOfTheWeek.getValue() - DayOfWeek.MONDAY.getValue());
        LocalDate currentWeekEndDate = currentWeekStartDate.plusDays(6);

        LocalDate previousWeekStartDate = currentWeekStartDate.minusDays(7);
        LocalDate previousWeekEndDate = currentWeekEndDate.minusDays(1);

        List<Object[]> previousData = orderRepository.getGraphicDataForSeller(seller.getUserId(), previousWeekStartDate, previousWeekEndDate);
        List<Long> previousWeek = new ArrayList<>(Collections.nCopies(7, 0L));

        for (Object[] o : previousData) {
            Long numberOfOrders = 0L;
            LocalDate localDate = null;

            numberOfOrders = (Long) o[0];
            Date dataVariable = (Date) o[1];
            localDate = new java.sql.Date(dataVariable.getTime()).toLocalDate();

            previousWeek.set(localDate.getDayOfWeek().getValue() - 1, numberOfOrders);
        }

        List<Object[]> currentData = orderRepository.getGraphicDataForSeller(seller.getUserId(), currentWeekStartDate, currentWeekEndDate);
        List<Long> currentWeek = new ArrayList<>(Collections.nCopies(7, 0L));
        //Get current day number
        int dayNumber = dayOfTheWeek.getValue();
        for (Object[] o : currentData) {
            Long numberOfOrders = 0L;
            LocalDate localDate = null;

            numberOfOrders = (Long) o[0];
            Date dataVariable = (Date) o[1];
            localDate = new java.sql.Date(dataVariable.getTime()).toLocalDate();

            currentWeek.set(localDate.getDayOfWeek().getValue() - 1, numberOfOrders);
        }

        for(int i = dayNumber; i < 7; i++){
            currentWeek.set(i, -1L);
        }

        SellerWeeklyNumberOfOrders sellerWeeklyNumberOfOrders = new SellerWeeklyNumberOfOrders(previousWeek, currentWeek);
        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Uspesno vraceni podaci za graficki prikaz broja porudzbina na nedeljnom nivou.")
                .data(sellerWeeklyNumberOfOrders).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity addNewPost(String token, String postText) {
        User user = jwtUtil.isTokenValid(token);

        Seller seller = sellerRepository.findSellerByUserUserId(user.getUserId());
        Post post = Post.builder()
                .text(postText)
                .dateTime(LocalDateTime.now())
                .seller(seller)
                .build();

        postRepository.save(post);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Post added successfully.")
                .data(null).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllProducts(String token, Long id) {

        Seller seller = null;
        boolean owner = false;
        if(id != null){
            seller = sellerRepository.getSellerById(id);
            if(token!=null){
                User user = jwtUtil.isTokenValid(token);
                if(user.getUserId() == id) owner = true;
            }
            if(seller == null){
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .code(HttpStatus.OK.value())
                        .success(true)
                        .message("Nema ovakvog prodavca!!!")
                        .data(null)
                        .build(),HttpStatus.OK);
            }
        }else{
            User user = jwtUtil.isTokenValid(token);
            owner = true;
            if(user != null){
                seller = sellerRepository.getSellerById(user.getUserId());
                if(seller == null){
                    return new ResponseEntity<>(SuccessResponse.builder()
                            .status(HttpStatus.OK.name())
                            .code(HttpStatus.OK.value())
                            .success(true)
                            .message("Nema ovakvog prodavca!!!")
                            .data(null)
                            .build(),HttpStatus.OK);
                }
            }
        }

        List<ProductDTO> list = new ArrayList<>();

        List<Product> products = productRepository.findAllBySeller(seller);

        for (Product p : products) {
            ProductDTO productDTO = ProductDTO.builder()
                    .sellerName(seller.getUser().getName())
                    .productName(p.getProductName())
                    .price(p.getPrice())
                    .id(p.getProductId())
                    .picture(fileSystem.getImageInBytes(String.valueOf(p.getProductId()), ImageType.PRODUCT))
                    .measurement(p.getMeasurement().getName())
                    .category(p.getCategory().getName())
                    .description(p.getDescription())
                    .category_id(p.getCategory().getCategoryId())
                    .available(p.isAvailable())
                    .owner(owner)
                    .build();
            list.add(productDTO);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("List of all products")
                .data(list)
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> getMyPersonalInformation(String token, Long idSeller) {

        User user = null;
        boolean follower=false;
        if(token != null){
            user = jwtUtil.isTokenValid(token);
        }
        Seller seller;

        if (idSeller != null && user != null) {
            seller = sellerRepository.getSellerById(idSeller);
            if (followRepository.existsById(new FollowKey(user.getUserId(),idSeller))) {
                follower = true;
            }
        } else if (idSeller == null && user != null) {
            seller = sellerRepository.getSellerById(user.getUserId());
        } else {
            seller = sellerRepository.getSellerById(idSeller);
        }

        if(seller == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .code(HttpStatus.OK.value())
                    .success(true)
                    .message("Personal information about seller with this id do not exist.")
                    .data(null).build(), HttpStatus.OK);
        }

        List<Follow> follows = followRepository.findAllBySeller_Id(seller.getId());
        List<Post> posts = postRepository.getPostBySeller(seller);

        double sum = 0.0;
        double br = 0;
        List<Product> products = productRepository.findAllBySeller(seller);
        for (Product p:products) {
            List<ProductComment> productComments = productCommentRepository.findByProduct_ProductId(p.getProductId());
            for (ProductComment pc: productComments) {
                sum += pc.getGrade();
                br++;
            }
        }

        double avg;
        if(br > 0) avg = sum / br;
        else avg = -1;

        boolean isProfileOwner = user != null && user.getUserId().equals(seller.getUser().getUserId());

        SelDTO selDTO;
        if(isProfileOwner == false){
            selDTO = SelDTO.builder()
                    .seller_id(seller.getUser().getUserId())
                    .picture(fileSystem.getImageInBytes(String.valueOf(seller.getUser().getUserId()), ImageType.USER))
                    .numberOfFollowers(follows.size())
                    .numberOfPosts(posts.size())
                    .surname(seller.getUser().getSurname())
                    .latitude(seller.getLatitude())
                    .longitude(seller.getLongitude())
                    .name(seller.getUser().getName())
                    .pib(seller.getPib())
                    .numberOfProducts(products.size())
                    .username(seller.getUser().getUsername())
                    .avgGrade(avg)
                    .isProfileOwner(false)
                    .isFollowed(follower)
                    .build();
        }else {
            selDTO = SelDTO.builder()
                    .seller_id(seller.getUser().getUserId())
                    .picture(fileSystem.getImageInBytes(String.valueOf(seller.getUser().getUserId()), ImageType.USER))
                    .numberOfFollowers(follows.size())
                    .numberOfPosts(posts.size())
                    .surname(user != null ? user.getSurname() : seller.getUser().getSurname())
                    .latitude(seller.getLatitude())
                    .longitude(seller.getLongitude())
                    .numberOfProducts(products.size())
                    .name(user != null ? user.getName() : seller.getUser().getName())
                    .pib(seller.getPib())
                    .username(user != null ? user.getUsername() : seller.getUser().getUsername())
                    .avgGrade(avg)
                    .isProfileOwner(true)
                    .isFollowed(follower)
                    .build();
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Personal information about seller.")
                .data(selDTO).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllProductsByCategoryId(String token, Long categoryId) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("seller"))
            throw new AccessDeniedException("Access Denied");

        Seller seller = sellerRepository.getSellerById(user.getUserId());

        List<ProductDTO> list = new ArrayList<>();

        List<Product> products = productRepository.findAllBySellerAndCategoryCategoryId(seller, categoryId);

        for (Product p : products) {
            ProductDTO productDTO = ProductDTO.builder()
                    .sellerName(seller.getUser().getName())
                    .productName(p.getProductName())
                    .picture(fileSystem.getImageInBytes(String.valueOf(p.getProductId()), ImageType.PRODUCT))
                    .price(p.getPrice())
                    .id(p.getProductId())
                    .measurement(p.getMeasurement().getName())
                    .category(p.getCategory().getName())
                    .description(p.getDescription())
                    .category_id(p.getCategory().getCategoryId())
                    .available(p.isAvailable())
                    .build();
            list.add(productDTO);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("List of all products")
                .data(list)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getPostDetails(String token, Long postID) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("seller"))
            throw new AccessDeniedException("Access Denied");

        Seller seller = sellerRepository.getSellerById(user.getUserId());

        List<PostDetailsDTO> list = new ArrayList<>();
        Post post = postRepository.getPostByPostId(postID);


        List<Like> likes = likeRepository.findAllByPost(post);
        List<PostComment> numberComm = postCommentRepository.getPostCommentsByPost(post);

        List<PostComment> postComments = postCommentRepository.getPostCommentsByPost(post);
        List<PostCommentDTO> postCommentDTOList = new ArrayList<>();
        for (PostComment pc : postComments) {
            PostCommentDTO postCommentDTO = PostCommentDTO.builder()
                    .picture(fileSystem.getImageInBytes(String.valueOf(pc.getUser().getUserId()), ImageType.USER))
                    .dateTime(pc.getDateTime())
                    .name(pc.getUser().getName())
                    .surname(pc.getUser().getSurname())
                    .text(pc.getText())
                    .username(pc.getUser().getUsername())
                    .build();

            postCommentDTOList.add(postCommentDTO);
        }

        PostDetailsDTO postDetailsDTO = PostDetailsDTO.builder()
                .commentsNumber(numberComm.size())
                .text(post.getText())
                .picture(fileSystem.getImageInBytes(String.valueOf(post.getSeller().getUser().getUserId()), ImageType.USER))
                .name(post.getSeller().getUser().getName())
                .postCommentDTOList(postCommentDTOList)
                .dateTime(post.getDateTime())
                .username(post.getSeller().getUser().getUsername())
                .surname(post.getSeller().getUser().getSurname())
                .likesNumber(likes.size())
                .latitude(post.getSeller().getLatitude())
                .longitude(post.getSeller().getLongitude())
                .build();

        list.add(postDetailsDTO);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("List of all posts with comments")
                .data(list)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllSellers() {
        List<SellerAddressDTO> sellerAddressDTOS = new ArrayList<>();

        List<Seller> sellers = sellerRepository.findAll();

        for(Seller s : sellers){
            SellerAddressDTO sellerAddressDTO = SellerAddressDTO.builder()
                    .seller_id(s.getUser().getUserId())
                    .latitude(s.getLatitude())
                    .longitude(s.getLongitude())
                    .picture(fileSystem.getImageInBytes(String.valueOf(s.getUser().getUserId()), ImageType.USER))
                    .username(s.getUser().getUsername())
                    .build();

            sellerAddressDTOS.add(sellerAddressDTO);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(sellerAddressDTOS)
                .message("List of all sellers")
                .code(HttpStatus.OK.value())
                .success(true)
                .status(HttpStatus.OK.name())
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> searchMyProducts(String token, String query) {
        User user = jwtUtil.isTokenValid(token);
        Seller seller = sellerRepository.findSellerByUserUserId(user.getUserId());

        List<Product> products = productRepository.getProductsBySellerAndQuery(query,seller.getId());

        List<ProductDTO> productDTOS = new ArrayList<>();

        for (Product p : products) {
           ProductDTO productDTO = ProductDTO.builder()
                        .id(p.getProductId())
                        .productName(p.getProductName())
                        .picture(fileSystem.getImageInBytes(String.valueOf(p.getProductId()), ImageType.PRODUCT))
                        .category(p.getCategory().getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .measurement(p.getMeasurement().getName())
                        .sellerName(p.getSeller().getUser().getName())
                        .category_id(p.getCategory().getCategoryId())
                        .build();

                productDTOS.add(productDTO);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(productDTOS)
                .message("List of products by query!")
                .code(HttpStatus.OK.value())
                .success(true)
                .status(HttpStatus.OK.name())
                .build(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> searchPostsByDate(String token, LocalDate date) {
        User user = jwtUtil.isTokenValid(token);

        Seller seller = sellerRepository.findSellerByUserUserId(user.getUserId());

        List<PostDTO> list = new ArrayList<>();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Post> posts = postRepository.findBySellerAndDateTimeBetween(seller,startOfDay,endOfDay);


        for (Post p : posts) {
            List<Like> likes = likeRepository.findAllByPost(p);
            List<PostComment> numberComm = postCommentRepository.getPostCommentsByPost(p);

            PostDTO post = PostDTO.builder()
                    .likesNumber(likes.size())
                    .commentsNumber(numberComm.size())
                    .dateTime(p.getDateTime())
                    .id(p.getPostId())
                    .text(p.getText())
                    .usernameSeller(p.getSeller().getUser().getUsername())
                    .build();

            list.add(post);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("List of all posts with comments")
                .data(list)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeAddress(String token, ChangeAddressRequest changeAddressRequest) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("seller"))
            throw new AccessDeniedException("Access Denied");

        Seller seller = sellerRepository.getSellerById(user.getUserId());
        if(changeAddressRequest.getLatitude() != 0 && changeAddressRequest.getLongitude() != 0){
            seller.setLatitude(changeAddressRequest.getLatitude());
            seller.setLongitude(changeAddressRequest.getLongitude());
            sellerRepository.save(seller);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Address changed successfully!")
                .data(null)
                .build(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeAvailabilityOfProduct(String token, Long productId) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("seller"))
            throw new AccessDeniedException("Access Denied");

        Product product = productRepository.getByProductId(productId);

        if(product.isAvailable()){
            product.setAvailable(false);
            productRepository.save(product);
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("Product is not available!")
                    .data(product.isAvailable())
                    .build(), HttpStatus.OK);
        }
        else{
            product.setAvailable(true);
            productRepository.save(product);
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("Product is available!")
                    .data(product.isAvailable())
                    .build(), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getAllOrders(String token) {
        User user = jwtUtil.isTokenValid(token);

        HashMap<String, List<OrdersSellerDTO>> orders = new HashMap<>();
        Seller seller = sellerRepository.findSellerByUserUserId(user.getUserId());

        List<Order> orderList = orderRepository.getOrdersBySeller(seller);
        List<OrdersSellerDTO> delivered = new ArrayList<>();
        List<OrdersSellerDTO> awaitingDelivery = new ArrayList<>();
        List<OrdersSellerDTO> onTheWay = new ArrayList<>();

        orders.put("delivered",delivered);
        orders.put("awaitingDelivery",awaitingDelivery);
        orders.put("onTheWay",onTheWay);


        for (Order o: orderList) {
            List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.getPurchaseOrderByOrderId(o);
            List<PurchaseDTO> purchaseDTOS = new ArrayList<>();

            for (PurchaseOrder po : purchaseOrderList) {
                PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                        .date(po.getOrderId().getOrderDate())
                        .measurement(po.getProduct().getMeasurement().getName())
                        .price(po.getProduct().getPrice())
                        .quantity(po.getQuantity())
                        .productName(po.getProduct().getProductName())
                        .build();

                purchaseDTOS.add(purchaseDTO);
            }

            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .id(o.getBuyer().getUserId())
                    .email(o.getBuyer().getEmail())
                    .latitude_buyer(o.getLatitudeBuyer())
                    .longitude_buyer(o.getLongitudeBuyer())
                    .surname(o.getBuyer().getSurname())
                    .username(o.getBuyer().getUsername())
                    .name(o.getBuyer().getName())
                    .role(o.getBuyer().getRole().getName())
                    .picture(fileSystem.getImageInBytes(String.valueOf(o.getBuyer().getUserId()), ImageType.USER))
                    .build();

            OrdersSellerDTO orderDTO = OrdersSellerDTO.builder()
                    .id(o.getId())
                    .buyerDTO(buyerDTO)
                    .purchaseDTOS(purchaseDTOS).build();

            String status = o.getOrderStatus().getName();
            if (status.equalsIgnoreCase("naruceno")) {
                awaitingDelivery.add(orderDTO);
            } else if (status.equalsIgnoreCase("dostavljeno")) {
                delivered.add(orderDTO);
            } else {
                onTheWay.add(orderDTO);
            }
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .data(orders)
                .message("List of all orders")
                .code(200)
                .success(true)
                .status(HttpStatus.OK.name())
                .build(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> setWorkingTime(String token, WorkingTimeRequest workingTimeRequest) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("seller"))
            throw new AccessDeniedException("Access Denied");

        Seller seller = sellerRepository.getSellerById(user.getUserId());

        WorkingTime wt = workingTimeRepository.findBySeller(seller);
        if(wt == null){
            WorkingTime workingTime = new WorkingTime();
            workingTime.setSeller(seller);
            if(workingTimeRequest.getMonday()==null){
                workingTime.setMonday("ZATVORENO");
            }else{
                workingTime.setMonday(workingTimeRequest.getMonday());
            }
            if(workingTimeRequest.getTuesday()==null){
                workingTime.setTuesday("ZATVORENO");
            }else{
                workingTime.setTuesday(workingTimeRequest.getTuesday());
            }
            if(workingTimeRequest.getWednesday()==null){
                workingTime.setWednesday("ZATVORENO");
            }else{
                workingTime.setWednesday(workingTimeRequest.getWednesday());
            }if(workingTimeRequest.getThursday()== null){
                workingTime.setThursday("ZATVORENO");
            }else{
                workingTime.setThursday(workingTimeRequest.getThursday());
            }if(workingTimeRequest.getFriday()==null){
                workingTime.setFriday("ZATVORENO");
            }else{
                workingTime.setFriday(workingTimeRequest.getFriday());
            }if(workingTimeRequest.getSaturday()==null){
                workingTime.setSaturday("ZATVORENO");
            }else{
                workingTime.setSaturday(workingTimeRequest.getSaturday());
            }
            if(workingTimeRequest.getSunday()==null){
                workingTime.setSunday("ZATVORENO");
            }else{
                workingTime.setSunday(workingTimeRequest.getSunday());
            }
            workingTimeRepository.save(workingTime);
        }else{
            if(workingTimeRequest.getMonday()==null){
                wt.setMonday("ZATVORENO");
            }else{
                wt.setMonday(workingTimeRequest.getMonday());
            }
            if(workingTimeRequest.getTuesday()==null){
                wt.setTuesday("ZATVORENO");
            }else{
                wt.setTuesday(workingTimeRequest.getTuesday());
            }
            if(workingTimeRequest.getWednesday()==null){
                wt.setWednesday("ZATVORENO");
            }else{
                wt.setWednesday(workingTimeRequest.getWednesday());
            }if(workingTimeRequest.getThursday() == null){
                wt.setThursday("ZATVORENO");
            }else{
                wt.setThursday(workingTimeRequest.getThursday());
            }if(workingTimeRequest.getFriday() == null){
                wt.setFriday("ZATVORENO");
            }else{
                wt.setFriday(workingTimeRequest.getFriday());
            }if(workingTimeRequest.getSaturday() == null){
                wt.setSaturday("ZATVORENO");
            }else{
                wt.setSaturday(workingTimeRequest.getSaturday());
            }
            if(workingTimeRequest.getSunday() == null){
                wt.setSunday("ZATVORENO");
            }else{
                wt.setSunday(workingTimeRequest.getSunday());
            }
            workingTimeRepository.save(wt);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Radno vreme uspesno setovano!")
                .data(null)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getWorkingTime(String token, Long id) {

        Seller seller = null;
        Long iden = 0L;
        if(id != null){
            seller = sellerRepository.getSellerById(id);
            if(seller == null){
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .code(HttpStatus.OK.value())
                        .success(true)
                        .message("Nema ovakvog prodavca!!!")
                        .data(null)
                        .build(),HttpStatus.OK);
            }
            iden = seller.getId();
        }else{
            User user = jwtUtil.isTokenValid(token);
            if(user != null){
                seller = sellerRepository.getSellerById(user.getUserId());
                if(seller == null){
                    return new ResponseEntity<>(SuccessResponse.builder()
                            .status(HttpStatus.OK.name())
                            .code(HttpStatus.OK.value())
                            .success(true)
                            .message("Nema ovakvog prodavca!!!")
                            .data(null)
                            .build(),HttpStatus.OK);
                }
                iden = seller.getId();
            }
        }

        WorkingTime wt = workingTimeRepository.findBySeller(seller);

        if(wt == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .success(true)
                    .status(HttpStatus.OK.name())
                    .message("Work time is not set yet")
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .build(), HttpStatus.OK);
        }
        WorkingTimeDTO workingTimeDTO = WorkingTimeDTO.builder()
                .monday(wt.getMonday())
                .tuesday(wt.getTuesday())
                .wednesday(wt.getWednesday())
                .thursday(wt.getThursday())
                .friday(wt.getFriday())
                .saturday(wt.getSaturday())
                .sunday(wt.getSunday())
                .build();

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Working time for seller")
                .data(workingTimeDTO)
                .build(),HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> getAllCustomerOrders(String token) {
        User user = jwtUtil.isTokenValid(token);

        Seller seller = sellerRepository.getSellerById(user.getUserId());
        if(seller == null){
            return new ResponseEntity<>(ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.name())
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Seller with this token does not exists").build(), HttpStatus.NOT_FOUND);
        }

        List<Order> orders = orderRepository.getOrderBySellerOrderByOrderDateDesc(seller);
        List<SellerCustomerOrderResponse> orderResponseList = new ArrayList<>();
        for (Order o : orders) {
            SellerCustomerOrderResponse orderResponse = SellerCustomerOrderResponse.builder()
                    .orderId(o.getId())
                    .username(o.getBuyer().getUsername())
                    .address(o.getBuyerAddress())
                    .date(FormatDateCustomUtil.formatDate(o.getOrderDate()))
                    .picture(fileSystem.getImageInBytes(String.valueOf(o.getBuyer().getPicture()), ImageType.USER))
                    .orderStatus(o.getOrderStatus().getOrderStatusId()).build();
            orderResponseList.add(orderResponse);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .data(orderResponseList).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getMyPosts(String token, Long id) {
        Seller seller = null;
        boolean owner = false;
        if(id != null){
            seller = sellerRepository.getSellerById(id);
            if(token!=null){
                User user = jwtUtil.isTokenValid(token);
                if(user.getUserId() == id) owner = true;
            }
            if(seller == null){
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .code(HttpStatus.OK.value())
                        .success(true)
                        .message("Nema ovakvog prodavca!!!")
                        .data(null)
                        .build(),HttpStatus.OK);
            }
        }else{
            User user = jwtUtil.isTokenValid(token);
            owner = true;
            if(user != null){
                seller = sellerRepository.getSellerById(user.getUserId());
                if(seller == null){
                    return new ResponseEntity<>(SuccessResponse.builder()
                            .status(HttpStatus.OK.name())
                            .code(HttpStatus.OK.value())
                            .success(true)
                            .message("Nema ovakvog prodavca!!!")
                            .data(null)
                            .build(),HttpStatus.OK);
                }
            }
        }

        List<PostDTO> list = new ArrayList<>();
        List<Post> posts = postRepository.getPostBySeller(seller);


        for (Post p : posts) {
            List<Like> likes = likeRepository.findAllByPost(p);
            List<PostComment> numberComm = postCommentRepository.getPostCommentsByPost(p);

            PostDTO post = PostDTO.builder()
                    .likesNumber(likes.size())
                    .commentsNumber(numberComm.size())
                    .dateTime(p.getDateTime())
                    .id(p.getPostId())
                    .text(p.getText())
                    .usernameSeller(p.getSeller().getUser().getUsername())
                    .owner(owner)
                    .build();

            list.add(post);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("List of all posts with comments")
                .data(list)
                .build(), HttpStatus.OK);
    }

}