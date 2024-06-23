package server.server.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import server.server.dtos.*;
import server.server.dtos.request.ChangeImageRequest;
import server.server.dtos.request.ChangeAddressRequest;
import server.server.dtos.request.DeliveryRatingRequest;
import server.server.dtos.request.UserUpdateRequest;
import server.server.dtos.response.PersonalInformationResponse;
import server.server.dtos.response.PurchaseOrderItemsResponse;
import server.server.dtos.response.SellerCustomerOrderDetailsAndItemsResponse;
import server.server.exceptions.AccessDeniedException;
import server.server.exceptions.PostNotFoundException;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtGenerator;
import server.server.jwt.JwtUtil;
import server.server.models.*;
import server.server.models.compositeKeys.FollowKey;
import server.server.models.compositeKeys.LikeKey;
import server.server.repository.*;
import server.server.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    DeliveryRatingRepository deliveryRatingRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    FileSystemUtil fileSystemUtil;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private DelivererRepository delivererRepository;

    @Autowired
    private DelivererOffersRepository delivererOffersRepository;

    @SneakyThrows
    @Override
    public ResponseEntity<?> myPersonalInformation(String token) {
        User user = jwtUtil.isTokenValid(token);

        List<Follow> follows = followRepository.getByUser_UserId(user.getUserId());
        List<Order> orders = orderRepository.getOrdersByBuyer_UserId(user.getUserId());

        //System.out.println(claims);
        return new ResponseEntity<>(UserDTO.builder()
                .picture(fileSystemUtil.getImageInBytes(String.valueOf(user.getUserId()), ImageType.USER))
                .name(user.getName())
                .email(user.getEmail())
                .surname(user.getSurname())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .id(user.getUserId())
                .numberOfFollows(follows.size())
                .numberOfOrders(orders.size())
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> myOrders(String token){
        HashMap<String, List<OrderDTO>> orders = new HashMap<>();

        User user = jwtUtil.isTokenValid(token);

        List<Order> orderList = orderRepository.getOrdersByBuyer_UserId(user.getUserId());
        List<OrderDTO> delivered = new ArrayList<>();
        List<OrderDTO> awaitingDelivery = new ArrayList<>();
        List<OrderDTO> onTheWay = new ArrayList<>();

        orders.put("delivered",delivered);
        orders.put("awaitingDelivery",awaitingDelivery);
        orders.put("onTheWay",onTheWay);

        for (Order o: orderList) {
            SellerDTO sellerDTO = SellerDTO.builder()
                    .surname(o.getSeller().getUser().getSurname())
                    .pib(o.getSeller().getPib())
                    .name(o.getSeller().getUser().getName())
                    .adress(o.getSeller().getAddress())
                    .picture(fileSystemUtil.getImageInBytes(String.valueOf(o.getSeller().getUser().getUserId()), ImageType.USER))
                    .username(o.getSeller().getUser().getUsername())
                    .latitude(o.getSeller().getLatitude())
                    .longitude(o.getSeller().getLongitude())
                    .email(o.getSeller().getUser().getEmail())
                    .build();

            List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.getPurchaseOrderByOrderId(o);
            List<PurchaseDTO> purchaseDTOS = new ArrayList<>();

            for (PurchaseOrder po : purchaseOrderList ) {
                PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                        .date(po.getOrderId().getOrderDate())
                        .measurement(po.getProduct().getMeasurement().getName())
                        .price(po.getProduct().getPrice())
                        .quantity(po.getQuantity())
                        .productName(po.getProduct().getProductName())
                        .build();

                purchaseDTOS.add(purchaseDTO);
            }

            OrderDTO orderDTO = OrderDTO.builder()
                    .id(o.getId())
                    .sellerDTO(sellerDTO)
                    .purchaseDTOS(purchaseDTOS).build();

            String status = o.getOrderStatus().getName();
            if(status.equalsIgnoreCase("naruceno")){
                awaitingDelivery.add(orderDTO);
            } else if (status.equalsIgnoreCase("dostavljeno")) {
                delivered.add(orderDTO);
            } else {
                onTheWay.add(orderDTO);
            }


        }

        return new ResponseEntity<>(orders,HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> update(String token, UserUpdateRequest userUpdateRequest) {
        User user = jwtUtil.isTokenValid(token);

        PersonalInformationResponse personalInformationResponse = PersonalInformationResponse.builder()
                .isEmailChanged(false)
                .isNameChanged(false)
                .isSurnameChanged(false)
                .isUsernameChanged(false)
                .newToken(null)
                .build();

        if(isEmailAlreadyTaken(userUpdateRequest.getEmail())){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("This email is already taken.")
                    .data(personalInformationResponse)
                    .build(), HttpStatus.OK);
        }

        if(isUsernameAlreadyTaken(userUpdateRequest.getUsername())){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("This username is already taken.")
                    .data(personalInformationResponse)
                    .build(), HttpStatus.OK);
        }


        if (userUpdateRequest.getName() != null && !userUpdateRequest.getName().equals("")) {
            if(validName(userUpdateRequest.getName())) {
                user.setName(userUpdateRequest.getName());
                personalInformationResponse.setNameChanged(true);
            }
            else{
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("New name is not valid.")
                        .data(personalInformationResponse)
                        .build(), HttpStatus.OK);
            }
        }
        if (userUpdateRequest.getSurname() != null && !userUpdateRequest.getSurname().equals("")) {
            if(validSurname(userUpdateRequest.getSurname())) {
                user.setSurname(userUpdateRequest.getSurname());
                personalInformationResponse.setSurnameChanged(true);
            }
            else{
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("New surname is not valid.")
                        .data(personalInformationResponse)
                        .build(), HttpStatus.OK);
            }

        }
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals("")) {
            if(validEmail(userUpdateRequest.getEmail())) {
                user.setEmail(userUpdateRequest.getEmail());
                personalInformationResponse.setEmailChanged(true);
            }
            else{
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("New email is not valid.")
                        .data(personalInformationResponse)
                        .build(), HttpStatus.OK);
            }
        }
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().equals("")) {
            if(validUsername(userUpdateRequest.getUsername())) {
                user.setUsername(userUpdateRequest.getUsername());
                personalInformationResponse.setUsernameChanged(true);
            }
            else{
                return new ResponseEntity<>(SuccessResponse.builder()
                        .status(HttpStatus.OK.name())
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("New username is not valid.")
                        .data(personalInformationResponse)
                        .build(), HttpStatus.OK);
            }
        }
        if(personalInformationResponse.isUsernameChanged() || personalInformationResponse.isEmailChanged() || personalInformationResponse.isSurnameChanged() || personalInformationResponse.isNameChanged()){
            User updatedUser = userRepository.save(user);
            if(personalInformationResponse.isUsernameChanged()){
                personalInformationResponse.setNewToken(jwtGenerator.generateJwtToken(updatedUser));
            }
            SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .message("Personal information changed successfully!")
                .data(personalInformationResponse)
                .code(200)
                .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }
        else{
            SuccessResponse successResponse = SuccessResponse.builder()
                    .success(true)
                    .status("OK")
                    .message("Personal information is not changed!")
                    .data(personalInformationResponse)
                    .code(200)
                    .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }
    }

    private boolean validEmail(String email) {
        String nameRegex = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";

        return Pattern.matches(nameRegex, email);
    }

    private boolean validUsername(String username) {
        String nameRegex = "^[a-zA-Z0-9!@#$%^&*()-_+=ćčžšđĆČŽŠĐ]*$";


        return Pattern.matches(nameRegex, username);

    }

    private boolean validSurname(String surname) {
        String surnameRegex = "^[a-zA-Z]{2,20}$";

        return Pattern.matches(surnameRegex, surname);
    }

    private boolean validName(String name) {
        String nameRegex = "^[a-zA-Z]{2,20}$";

        return Pattern.matches(nameRegex, name);
    }

    @Override
    public ResponseEntity<?> getLastNews(String token) {
        User user = jwtUtil.isTokenValid(token);
//        if (!user.getRole().getName().equalsIgnoreCase("user"))
//            throw new AccessDeniedException("Access Denied");

        //lista proizvodjaca koje prati
        List<Follow> follows = followRepository.getByUser_UserId(user.getUserId());
        List<PostDTO> allPosts = new ArrayList<>();
        //Long sellerId;

        for (Follow f: follows) {

            List<Post> posts = postRepository.getPostBySeller(f.getSeller());
            for(Post p : posts){
                List<Like> likes = likeRepository.findAllByPost(p);
                List<PostComment> postComments = postCommentRepository.getPostCommentsByPost(p);

                boolean isLiked = likeRepository.existsById(new LikeKey(p.getPostId(),user.getUserId()));

                PostDTO post = PostDTO.builder()
                        .likedPost(isLiked)
                        .likesNumber(likes.size())
                        .commentsNumber(postComments.size())
                        .dateTime(p.getDateTime())
                        .id(p.getPostId())
                        .text(p.getText())
                        .usernameSeller(p.getSeller().getUser().getUsername())
                        .build();

                allPosts.add(post);
            }
        }
        if(allPosts.isEmpty()){
            SuccessResponse successResponse = SuccessResponse.builder()
                    .success(true)
                    .status("OK")
                    .message("success")
                    .data(allPosts)
                    .code(200)
                    .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }

        allPosts.sort(Comparator.comparing(PostDTO::getDateTime).reversed());

        int limit = Math.min(allPosts.size(),5);

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .message("success")
                .data(allPosts.subList(0,limit))
                .code(200)
                .build();
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> getMoreInformationAboutPost(String token, Long postId) {
        User user = jwtUtil.isTokenValid(token);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        List<PostCommentDTO> postCommentsDTOS = new ArrayList<>();

        List<PostComment> postComments = postCommentRepository.getPostCommentsByPost(post);
        for (PostComment p : postComments){
            PostCommentDTO postCommentDTO = PostCommentDTO.builder()
                    .dateTime(p.getDateTime())
                    .name(p.getUser().getName())
                    .picture(fileSystemUtil.getImageInBytes(String.valueOf(p.getUser().getUserId()), ImageType.USER))
                    .surname(p.getUser().getSurname())
                    .text(p.getText())
                    .username(p.getUser().getUsername())
                    .build();

            postCommentsDTOS.add(postCommentDTO);
        }

        List<Like> likes = likeRepository.findAllByPost(post);
        List<PostComment> numberComm = postCommentRepository.getPostCommentsByPost(post);

        boolean isLiked = likeRepository.existsById(new LikeKey(post.getPostId(),user.getUserId()));

        PostDetailsDTO postDetailsDTO = PostDetailsDTO.builder()
                .likedPost(isLiked)
                .commentsNumber(numberComm.size())
                .text(post.getText())
                .name(post.getSeller().getUser().getName())
                .postCommentDTOList(postCommentsDTOS)
                .picture(fileSystemUtil.getImageInBytes(String.valueOf(post.getSeller().getUser().getUserId()), ImageType.USER))
                .dateTime(post.getDateTime())
                .username(post.getSeller().getUser().getUsername())
                .surname(post.getSeller().getUser().getSurname())
                .likesNumber(likes.size())
                .latitude(post.getSeller().getLatitude())
                .longitude(post.getSeller().getLongitude())
                .build();

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .message("success")
                .data(postDetailsDTO)
                .code(200)
                .build();
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> likePost(String token, Long id) {
        User user = jwtUtil.isTokenValid(token);

        if (!likeRepository.existsById(new LikeKey(id,user.getUserId()))) {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new PostNotFoundException("Post not found"));

            Like like = new Like();
            like.setLikeKey(new LikeKey(id,user.getUserId()));
            like.setUser(user);
            like.setPost(post);
            like.setDateTime(LocalDateTime.now());

            likeRepository.save(like);
            SuccessResponse successResponse = SuccessResponse.builder()
                    .code(200)
                    .success(true)
                    .status("OK")
                    .data(null)
                    .message("Post liked successfully!")
                    .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);


        } else {
            unlikePost(user, id);

            SuccessResponse successResponse = SuccessResponse.builder()
                    .code(200)
                    .success(true)
                    .status("OK")
                    .data(null)
                    .message("Post unliked successfully!")
                    .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getGraphicDataForBuyer(String token) {
        User buyer = jwtUtil.isTokenValid(token);

        List<Object[]> report = orderRepository.getGraphicDataForBuyer(buyer.getUserId());

        List<Category> categoryList= (List<Category>) categoryRepository.findAll();
        List<BuyerGraphicDataDTO> buyerGraphicDataDTOList = new ArrayList<>();

        //Setovanje svih kategorija i broja porudzbina na 0
        for (Category category : categoryList) {
            BuyerGraphicDataDTO graphicData = new BuyerGraphicDataDTO(category.getCategoryId(), 0L);
            buyerGraphicDataDTOList.add(graphicData);
        }

        for (Object o[] : report) {
            Long categoryId = (Long) o[0];
            Long numberOfOrders = ((BigDecimal) o[1]).longValue();

            buyerGraphicDataDTOList.set((int) (categoryId - 1), new BuyerGraphicDataDTO(categoryId, numberOfOrders));
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .data(buyerGraphicDataDTOList)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> followSeller(String token, Long id) {
        User user = jwtUtil.isTokenValid(token);

        if (!followRepository.existsById(new FollowKey(user.getUserId(),id))) {
            Follow follow = new Follow();
            follow.setFollowKey(new FollowKey(user.getUserId(), id));
            follow.setUser(user);
            follow.setSeller(sellerRepository.getSellerById(id));


            followRepository.save(follow);
            SuccessResponse successResponse = SuccessResponse.builder()
                    .code(200)
                    .success(true)
                    .status("OK")
                    .data(null)
                    .message("Seller followed successfully!")
                    .build();
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        else {
            unfollowUser(user, id);

            SuccessResponse successResponse = SuccessResponse.builder()
                    .code(200)
                    .success(true)
                    .status("OK")
                    .data(null)
                    .message("Seller unfollowed successfully!")
                    .build();
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }
    }
/*
    @Override
    public ResponseEntity<?> changePassword(String token, ChangePasswordRequest changePasswordRequest) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.name())
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("Ooops... Looks like the token is missing.")
                    .build(), HttpStatus.UNAUTHORIZED);
        }

        Claims claims = jwtUtil.decode(token);

       String role = (String) claims.get("role");

        if (!role.equalsIgnoreCase("user")) {
            throw new AccessDeniedException("Access Denied");
        }

        String username = (String) claims.get("sub");
        User user = userRepository.findByUsername(username);

        System.out.println(changePasswordRequest.getOldPassword());
        System.out.println(user.getPassword());



        if (!BCrypt.checkpw(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>(ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.name())
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Ooops... wrong password.")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        String newPasswordHash = BCrypt.hashpw(changePasswordRequest.getNewPassword(), BCrypt.gensalt());
        user.setPassword(newPasswordHash);

        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Password changed successfully")
                .data(null)
                .build(), HttpStatus.OK);

    }
*/
    @Override
    public ResponseEntity<?> validateOldPassword(String token, String oldPassword) {
        User user = jwtUtil.isTokenValid(token);

        boolean goodOldPassword = false;
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("Old password is not valid.")
                    .data(goodOldPassword)
                    .build(), HttpStatus.OK);
        }

        goodOldPassword = true;
        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Old password is valid.")
                .data(goodOldPassword)
                .build(), HttpStatus.OK);

}

    @Override
    public ResponseEntity<?> changePassword(String token, String newPassword) {
        User user = jwtUtil.isTokenValid(token);

        if(BCrypt.checkpw(newPassword, user.getPassword())){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("New password is same as the old password.")
                    .data(true)
                    .build(), HttpStatus.OK);
        }

        if (!isPasswordValid(newPassword)) {
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("New password is not valid.")
                    .data(false)
                    .build(), HttpStatus.OK);
        }

        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(newPasswordHash);

        User updatedUser = userRepository.save(user);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("New password is valid.")
                .data(true)
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> changeProfileImage(String token, ChangeImageRequest image) {
        User user = jwtUtil.isTokenValid(token);

        fileSystemUtil.saveImage(String.valueOf(user.getUserId()), image.getImage(), ImageType.USER);

        byte[] changedImage = fileSystemUtil.getImageInBytes(String.valueOf(user.getUserId()), ImageType.USER);
        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Profile image changed successfully")
                .data(changedImage).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> rateDelivery(String token, DeliveryRatingRequest deliveryRatingRequest) {
        User user = jwtUtil.isTokenValid(token);

        Order order = orderRepository.getOrderById(deliveryRatingRequest.getOrderId());
        if(order == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .message("Nema ovakve porudzbine")
                    .data(null).build(), HttpStatus.OK);
        }
        Deliverer deliverer = delivererRepository.getById(order.getDelivererId().getId());
        if(deliverer == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .message("Nema ovakvog dostavljaca")
                    .data(null).build(), HttpStatus.OK);
        }

        DeliveryRating deliveryRating = new DeliveryRating();
        deliveryRating.setDeliverer(deliverer);
        deliveryRating.setUser(user);
        deliveryRating.setGrade(deliveryRatingRequest.getGrade());
        deliveryRating.setComment(deliveryRatingRequest.getComment());

        deliveryRatingRepository.save(deliveryRating);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Dostavljac je ocenjen.")
                .data(true)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> markOrderAsDelivered(String token, Long orderId) {
        User user = jwtUtil.isTokenValid(token);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            throw new AccessDeniedException("Access Denied");
        }

        Order order = optionalOrder.get();

        order.setOrderStatus(new OrderStatus(3l,"DOSTAVLJENO"));
        orderRepository.save(order);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Order marked as delivered.")
                .data(true)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> allOffers(String token) {
        User user = jwtUtil.isTokenValid(token);

        List<Order> orders = orderRepository.getOrdersByBuyer_UserId(user.getUserId());

        List<OfferDTO> offerDTOS = new ArrayList<>();
        for(Order o  : orders){
            List<DelivererOffer> offers = delivererOffersRepository.getDelivererOfferByOrder_IdAndStatus_OfferStatusId(o.getId(),1L);

            for(DelivererOffer df : offers){
                double totalPrice = 0;
                List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.getPurchaseOrderByOrderId(o);

                for(PurchaseOrder pu : purchaseOrders){
                    totalPrice += pu.getProduct().getPrice()*pu.getQuantity();
                }

                Date date = df.getOrder().getOrderDate();
                LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
                String orderDate = localDate.getDayOfMonth() + "/" + localDate.getMonth().getValue() + "/" + localDate.getYear();

                OfferDTO offerDTO = OfferDTO.builder()
                        .offerStatusId(1L)
                        .delivererName(df.getDeliverer().getUser().getName())
                        .orderDate(orderDate)
                        .delivererPrice(df.getPrice())
                        .orderPrice(totalPrice)
                        .delivererSurname(df.getDeliverer().getUser().getSurname())
                        .delivererUsername(df.getDeliverer().getUser().getUsername())
                        .delivererPicture(fileSystemUtil.getImageInBytes(String.valueOf(df.getDeliverer().getUser().getUserId()), ImageType.USER))
                        .offerId(df.getOfferId())
                        .orderId(o.getId())
                        .delivererId(df.getDeliverer().getId())
                        .build();

                offerDTOS.add(offerDTO);
            }
        }

        SuccessResponse successResponse = SuccessResponse.builder()
                .code(200)
                .success(true)
                .status("OK")
                .data(offerDTOS)
                .message("List of all offers!")
                .build();
        return new ResponseEntity<>(successResponse,HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> setMyAddress(String token, ChangeAddressRequest changeAddressRequest) {
        User user = jwtUtil.isTokenValid(token);

        if(changeAddressRequest.getLatitude() != 0 && changeAddressRequest.getLongitude() != 0){
            user.setLatitudeB(changeAddressRequest.getLatitude());
            user.setLongitudeB(changeAddressRequest.getLongitude());
            userRepository.save(user);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Address added successfully!")
                .data(null)
                .build(),HttpStatus.OK);
    }

    private void unfollowUser(User user, Long id) {
        if (followRepository.existsById(new FollowKey(user.getUserId(),id))) {
            FollowKey followKey = new FollowKey(user.getUserId(), id);
            Follow follow = followRepository.findById(followKey).get();
            followRepository.delete(follow);
        }
    }

    public void unlikePost(User user, Long postId) {
        if (likeRepository.existsById(new LikeKey(postId,user.getUserId()))) {
            LikeKey likeKey = new LikeKey(postId,user.getUserId());
            Like like = likeRepository.findById(likeKey).get();
            likeRepository.delete(like);
        }
    }

    private boolean isPasswordValid(String newPassword) {
        return newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    private boolean isEmailAlreadyTaken(String email) {
        return userRepository.findByEmail(email) != null ? true : false;
    }

    private boolean isUsernameAlreadyTaken(String username) {
        return userRepository.findByUsernameCustom(username) != null ? true : false;
    }
}
