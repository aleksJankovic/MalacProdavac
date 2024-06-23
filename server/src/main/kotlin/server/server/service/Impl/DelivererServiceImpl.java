package server.server.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.AccRejDTO;
import server.server.dtos.AllAndThisMonthEarningsDTO;
import server.server.dtos.DelivererDTO;
import server.server.dtos.request.DelivererUpdateRequest;
import server.server.dtos.request.TipForJobRequest;
import server.server.dtos.response.*;
import server.server.exceptions.AccessDeniedException;
import server.server.exceptions.EmailUsernameAlreadyTakenException;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.*;
import server.server.models.compositeKeys.DriverLicensesKey;
import server.server.notifications.NotificationMessage;
import server.server.notifications.service.FirebaseMessagingService;
import server.server.repository.*;
import server.server.service.DelivererService;
import server.server.utils.FormatDateCustomUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class DelivererServiceImpl implements DelivererService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DelivererRepository delivererRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DelivererOffersRepository delivererOffersRepository;

    @Autowired
    FileSystemUtil fileSystem;
    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private DeliveryRatingRepository deliveryRatingRepository;
    @Autowired
    private DelivererOfferStatusRepository delivererOfferStatusRepository;

    @Autowired
    private DriversLicensesRepository driversLicensesRepository;

    @Autowired
    private DrivingCategoryRepository drivingCategoryRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private FirebaseMessagingService firebaseMessagingService;
    @Autowired
    private NotificationRepository notificationRepository;

    @SneakyThrows
    @Override
    public ResponseEntity<?> delivererPersonalInformation(String token, Long id) {
        User user = null;
        boolean owner=false;
        if(token != null){
            user = jwtUtil.isTokenValid(token);
        }
        Deliverer deliverer;

        if (id != null && user != null) {
            deliverer = delivererRepository.getById(id);
            if(id == user.getUserId()){
                owner = true;
            }
        } else if (id == null && user != null) {
            deliverer = delivererRepository.getByUser(user);
            owner = true;
        } else {
            deliverer = delivererRepository.getById(id);
        }

        if(deliverer == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .code(200)
                    .data(null)
                    .message("Nema dostavljaca sa ovim id ili tokenom")
                    .status("OK")
                    .success(true)
                    .build(),HttpStatus.OK);
        }

        List<DeliveryRating> deliveryRatings = deliveryRatingRepository.findAllByDeliverer(deliverer);

        double sum = 0;
        int br = 0;
        for(DeliveryRating dr : deliveryRatings){
            sum+=dr.getGrade();
            br++;
        }

        double avg = -1;
        if(br>0) avg = sum/br;


        //System.out.println(claims);
        DelivererDTO delivererDTO = DelivererDTO.builder()
                .id(deliverer.getUser().getUserId())
                .picture(fileSystem.getImageInBytes(String.valueOf(deliverer.getUser().getUserId()), ImageType.USER))
                .role(deliverer.getUser().getRole().getName())
                .surname(deliverer.getUser().getSurname())
                .username(deliverer.getUser().getUsername())
                .name(deliverer.getUser().getName())
                .email(deliverer.getUser().getEmail())
                .longitude(deliverer.getLongitude())
                .latitude(deliverer.getLatitude())
                .location(deliverer.getLocation())
                .avgGrade(avg)
                .owner(owner)
                .build();

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(200)
                .data(delivererDTO)
                .message("success")
                .status("OK")
                .success(true)
                .build(),HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> updateDelivererInformation(String token, DelivererUpdateRequest delivererUpdateRequest) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("Deliverer"))
            throw new AccessDeniedException("Access Denied");

        Deliverer deliverer = delivererRepository.getByUser(user);

        boolean isUsernameAlreadyTaken = false;
        if(delivererUpdateRequest.getUsername() != null){
            if(user.getUsername().equals(delivererUpdateRequest.getUsername()) == false){
                isUsernameAlreadyTaken = isUsernameAlreadyTaken(delivererUpdateRequest.getUsername());
            }
        }

        boolean isEmailAlreadyTaken = false;
        if (delivererUpdateRequest.getEmail() != null) {
            if(user.getEmail().equals(delivererUpdateRequest.getEmail()) == false){
                isEmailAlreadyTaken = isEmailAlreadyTaken(delivererUpdateRequest.getEmail());
            }
        }

        EmailUsernameAvailabilityResponse response = new EmailUsernameAvailabilityResponse(isUsernameAlreadyTaken, isEmailAlreadyTaken);
        if(isUsernameAlreadyTaken || isEmailAlreadyTaken)
            throw new EmailUsernameAlreadyTakenException("Email or username is already in use.", response);

        if (delivererUpdateRequest.getName() != null && !delivererUpdateRequest.getName().equals("")) {
            deliverer.getUser().setName(delivererUpdateRequest.getName());
        }
        if (delivererUpdateRequest.getSurname() != null && !delivererUpdateRequest.getSurname().equals("")) {
            deliverer.getUser().setSurname(delivererUpdateRequest.getSurname());
        }
        if (delivererUpdateRequest.getUsername() != null && !delivererUpdateRequest.getUsername().equals("")) {
            deliverer.getUser().setUsername(delivererUpdateRequest.getUsername());
        }
        if(delivererUpdateRequest.getPassword() != null && !delivererUpdateRequest.getPassword().equals("")){
            deliverer.getUser().setPassword(delivererUpdateRequest.getPassword());
        }
        if (delivererUpdateRequest.getEmail() != null && !delivererUpdateRequest.getEmail().equals("")) {
            deliverer.getUser().setEmail(delivererUpdateRequest.getEmail());
        }
        if (delivererUpdateRequest.getLongitude() != 0) {
            deliverer.setLongitude(delivererUpdateRequest.getLongitude());
        }
        if (delivererUpdateRequest.getLatitude() != 0) {
            deliverer.setLatitude(delivererUpdateRequest.getLatitude());
        }



        Deliverer updatedDeliverer = delivererRepository.save(deliverer);
        fileSystem.saveImage(String.valueOf(updatedDeliverer.getUser().getUserId()), delivererUpdateRequest.getPicture(), ImageType.USER);

        DelivererDTO delivererDTO = DelivererDTO.builder()
                                .name(updatedDeliverer.getUser().getName())
                                .surname(updatedDeliverer.getUser().getSurname())
                                .username(updatedDeliverer.getUser().getUsername())
                                .longitude(updatedDeliverer.getLongitude())
                                .latitude(updatedDeliverer.getLatitude())
                                .email(updatedDeliverer.getUser().getEmail())
                                .picture(fileSystem.getImageInBytes(updatedDeliverer.getUser().getUsername(), ImageType.USER))
                                .build();

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .message("success")
                .data(delivererDTO)
                .code(200)
                .build();
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> getListOfJobs(String token) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("Deliverer"))
            throw new AccessDeniedException("Access Denied");

        OrderStatus orderStatus = orderStatusRepository.findById(4L).get();
        List<Order> listOfOrders = orderRepository.getOrdersByOrderStatusOrderByOrderDateDesc(orderStatus);
        List<DelivererJobResponse> listOfJobs = new ArrayList<>();
        boolean flag = false;
        for (Order order : listOfOrders) {
            flag = false;
            DelivererOffer exists = delivererOffersRepository.getDelivererOfferByDeliverer_IdAndOrder_Id(user.getUserId(), order.getId());
            if(exists != null){
                System.out.println("Trazim: "+user.getUserId() + order.getId() + "Naso: " + exists.getDeliverer().getId() + exists.getOrder().getId() );
                flag = true;
            }

            DelivererJobResponse job = DelivererJobResponse.builder()
                    .orderId(order.getId())
                    .buyerUsername(order.getBuyer().getUsername())
                    .buyerAddress(order.getBuyerAddress())
                    .sellerLong(order.getSeller().getLongitude())
                    .sellerLat(order.getSeller().getLatitude())
                    .buyerImage(fileSystem.getImageInBytes(String.valueOf(order.getBuyer().getUserId()), ImageType.USER))
                    .sentOffer(flag).build();
            listOfJobs.add(job);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Lista porudzbina koje su u potrazi za dostavljacem")
                .data(listOfJobs).build(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> sendTipForJob(String token, TipForJobRequest tipForJobRequest) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("Deliverer"))
            throw new AccessDeniedException("Access Denied");

        Deliverer deliverer = delivererRepository.getByUser(user);
        Order order = orderRepository.getOrderById(tipForJobRequest.getOrderId());

        String dateString = String.valueOf(tipForJobRequest.getDate());
        LocalDateTime localDateTime = LocalDateTime.parse(dateString + "T00:00:00");
        localDateTime = localDateTime.with(LocalTime.MIDNIGHT);

        DelivererOffer delivererOffer = DelivererOffer.builder()
                .deliverer(deliverer)
                .order(order)
                .comment(tipForJobRequest.getComment())
                .price(tipForJobRequest.getPrice())
                .dateTime(localDateTime)
                .status(DelivererOfferStatus.builder()
                        .offerStatusId(1L)
                        .name("PONUDA_POSLATA")
                        .build())
                .build();

        delivererOffersRepository.save(delivererOffer);

        try{
            NotificationMessage notificationMessage = NotificationMessage.builder()
                    .recipientToken(notificationRepository.findById(order.getBuyer().getUserId()).get().getRecipientToken())
                    .title("Ponuda za dostavu porudžbine: #" + order.getId())
                    .body("Korisnik "+ user.getName() +" " + user.getSurname() + " je poslao ponudu za dostavu. Cena dostave je:" +delivererOffer.getPrice() + " rsd. \n Komentar: " + delivererOffer.getComment())
                    .image("")
                    .data(null).build();

            firebaseMessagingService.sendNotificationByToken(notificationMessage);
        }
        catch (Exception e){
        }

        SuccessResponse successResponse = SuccessResponse.builder()
                .code(200)
                .success(true)
                .status("OK")
                .data(null)
                .message("Tip for job sent!")
                .build();
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDeliveryOffers(String token) {
        User user = jwtUtil.isTokenValid(token);
        List<DelivererOffer> offers = delivererOffersRepository.getDelivererOfferByOrder_BuyerOrderByDateTimeDesc(user);

        List<DeliveryOfferResponse> responseList = new ArrayList<>();

        for (DelivererOffer offer : offers) {
            List<DeliveryRating> deliveryRatings = deliveryRatingRepository.findAllByDeliverer(offer.getDeliverer());

            double sum = 0;
            int br = 0;
            for(DeliveryRating dr : deliveryRatings){
                sum+=dr.getGrade();
                br++;
            }

            double avg = -1;
            if(br>0) avg = sum/br;

            DeliveryOfferResponse response = DeliveryOfferResponse.builder()
                    .delivererImage(fileSystem.getImageInBytes(String.valueOf(offer.getDeliverer().getUser().getUserId()), ImageType.USER))
                    .delivererAvgGrade(avg)
                    .delivererId(offer.getDeliverer().getId())
                    .delivererUsername(offer.getDeliverer().getUser().getUsername())
                    .offerStatus(offer.getStatus().getOfferStatusId())
                    .date(FormatDateCustomUtil.formatDate(offer.getDateTime()))
                    .price(offer.getPrice())
                    .orderId(offer.getOrder().getId())
                    .offerId(offer.getOfferId()).build();

            responseList.add(response);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Sve ponude za dostavljca od najsvezijih do najstarijih.")
                .data(responseList).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeDeliveryOfferStatus(String token, Long offerId, boolean offerAccepted) {
        User user = jwtUtil.isTokenValid(token);
        DelivererOffer offer = delivererOffersRepository.findById(offerId).get();

        Long statusId = offerAccepted ? 2L : 3L;
        offer.setStatus(delivererOfferStatusRepository.findById(statusId).get());

        delivererOffersRepository.save(offer);

        if(offerAccepted){
            try{
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .recipientToken(notificationRepository.findById(offer.getDeliverer().getId()).get().getRecipientToken())
                        .title("Potvrda ponude za dostavu")
                        .body("Korisnik "+ offer.getOrder().getBuyer().getName() +" " + offer.getOrder().getBuyer().getSurname() + " je prihvatio ponudu za dostavu.")
                        .image("")
                        .data(null).build();

                firebaseMessagingService.sendNotificationByToken(notificationMessage);
            }
            catch (Exception e){
            }

            Order order = orderRepository.getOrderById(offer.getOrder().getId());
            order.setOrderStatus(new OrderStatus(2L,"POSLATO"));
            orderRepository.save(order);
            List<DelivererOffer> delivererOffers = delivererOffersRepository.getDelivererOffersByOrder_Id(order.getId());
            for(DelivererOffer df : delivererOffers){
                if(df.getOfferId() != offerId){
                    df.setStatus(new DelivererOfferStatus(3L, "ODBIJENA_PONUDA"));
                    delivererOffersRepository.save(df);
                }
            }
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .success(true)
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .data(null)
                .message("Status porudzbine uspesno promenjen").build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> confirmOrderReceiving(String token, Long offerId) {
        User user = jwtUtil.isTokenValid(token);
        DelivererOffer offer = delivererOffersRepository.findById(offerId).get();
        Order order = offer.getOrder();


        order.setOrderStatus(orderStatusRepository.findById(3L).get());
        offer.setStatus(delivererOfferStatusRepository.findById(4L).get());

        orderRepository.save(order);
        delivererOffersRepository.save(offer);

        return new ResponseEntity<>(SuccessResponse.builder()
                .success(true)
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .data(null)
                .message("Prijem posiljke uspesno potvrdjen.").build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDrivingCategoriesByUser(String token, Long id) {
        User user = null;
        boolean owner = false;
        if(token != null){
            user = jwtUtil.isTokenValid(token);
        }
        Deliverer deliverer;

        if (id != null && user != null) {
            deliverer = delivererRepository.getById(id);
            if(id == user.getUserId()){
                owner = true;
            }
        } else if (id == null && user != null) {
            deliverer = delivererRepository.getByUser(user);
            owner = true;
        } else {
            deliverer = delivererRepository.getById(id);
        }

        List<DriversLicenses> licenses = driversLicensesRepository.getDriversLicensesByUser(user);

        HashMap<String, Boolean> categoriesMap = new HashMap<>();
        categoriesMap.put("CAR", false);
        categoriesMap.put("MOTORCYCLE", false);
        categoriesMap.put("VAN", false);
        categoriesMap.put("TRUCK", false);

        licenses.forEach(license -> {
            String categoryName = license.getDrivingCategory().getCategoryName();
            categoriesMap.put(categoryName, true);
        });

        return new ResponseEntity<>(SuccessResponse.builder()
                .success(true)
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .data(categoriesMap)
                .message("Vozacke dozvole za dostavljaca")
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateDrivingCategories(String token, HashMap<String,Boolean> drivingLicensesRequest) {
        User user = jwtUtil.isTokenValid(token);
        if(!user.getRole().getName().equalsIgnoreCase("Deliverer"))
            throw new AccessDeniedException("Access Denied");

        Deliverer deliverer = delivererRepository.getByUser(user);
        List<DriversLicenses> licenses = driversLicensesRepository.getDriversLicensesByUser(user);

        HashMap<String, Boolean> updatedCategories = drivingLicensesRequest;
        if(updatedCategories != null) {
            for (HashMap.Entry<String, Boolean> entry : updatedCategories.entrySet()) {
                String categoryName = entry.getKey();
                Boolean hasLicense = entry.getValue();

                DrivingCategory drivingCategory = drivingCategoryRepository.findByCategoryName(categoryName);

                if (drivingCategory != null) {

                    DriverLicensesKey key = new DriverLicensesKey(user.getUserId(), drivingCategory.getDrivingCategoryId());
                    Optional<DriversLicenses> existingRecord = driversLicensesRepository.findById(key);

                    if (hasLicense) {
                        if (existingRecord.isEmpty()) {
                            DriversLicenses driversLicenses = new DriversLicenses(key, user, drivingCategory);
                            driversLicensesRepository.save(driversLicenses);
                        }
                    } else {
                        existingRecord.ifPresent(driversLicensesRepository::delete);

                    }
                }
            }
        }
        return new ResponseEntity<>(SuccessResponse.builder()
                .success(true)
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .data(null)
                .message("Vozacke dozvole za dostavljaca")
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDeliverersPerformanceAnalyticsGradePercentage(String token, Long delivererId) {
        User user = jwtUtil.isTokenValid(token);

        List<Object[]> response = deliveryRatingRepository.getDeliverersPerformanceAnalyticsGradeNumber(delivererId);
        List<AnalyticsGradePercentageResponse> analytics = new ArrayList<>();
        for (Object[] o : response) {
            Long grade = Math.round((Double) o[0]);
            BigDecimal percentageBigDecimal = (BigDecimal) o[2];

            Double percentage = percentageBigDecimal.doubleValue();


            analytics.add(AnalyticsGradePercentageResponse.builder()
                    .grade(grade)
                    .percentage(percentage).build());
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Draga koleginice Aleksandra obaveštavam Vas da ste uspešno pogodili željeni API. Čestitamo Vam, Vaš Bekend tim! :) ")
                .data(analytics)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAcceptedAndRejectedOffers(String token, Long id) {
        User user = null;
        boolean owner=false;

        if(token != null){
            user = jwtUtil.isTokenValid(token);
        }

        Deliverer deliverer;

        if (id != null && user != null) {
            deliverer = delivererRepository.getById(id);
            if(id == user.getUserId()){
                owner = true;
            }
        } else if (id == null && user != null) {
            deliverer = delivererRepository.getByUser(user);
            owner = true;
        } else {
            deliverer = delivererRepository.getById(id);
        }

        if(deliverer == null){
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .code(HttpStatus.OK.value())
                    .success(true)
                    .message("Nema ovakvog dostavljaca!!")
                    .data(null)
                    .build(), HttpStatus.OK);
        }
        List<DelivererOffer> accepted = delivererOffersRepository.getDelivererOfferByDelivererAndStatus_OfferStatusId(deliverer, 2L);

        List<DelivererOffer> rejected = delivererOffersRepository.getDelivererOfferByDelivererAndStatus_OfferStatusId(deliverer, 3L);

        AccRejDTO accRejDTO = AccRejDTO.builder()
                .accepted(accepted.size())
                .rejected(rejected.size())
                .owner(owner)
                .build();

        return new ResponseEntity<>(SuccessResponse.builder()
                .message("Broj odobrenih i odbijenih ponuda")
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .data(accRejDTO)
                .success(true)
                .build(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> calculateTotalEarningsForCurrentMonth(String token) {
        User user = jwtUtil.isTokenValid(token);

        Deliverer deliverer = delivererRepository.getByUser(user);

        YearMonth currentYearMonth = YearMonth.now();
        LocalDateTime startOfMonth = LocalDateTime.of(currentYearMonth.atDay(1), LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.of(currentYearMonth.atEndOfMonth(), LocalTime.MAX);

        List<DelivererOffer> delivererOffers = delivererOffersRepository.getDelivererOfferByDateTimeBetweenAndStatus_OfferStatusIdAndDeliverer(startOfMonth,endOfMonth,4L,deliverer);

        List<DelivererOffer> delivererOffers2 = delivererOffersRepository.getDelivererOfferByDelivererAndStatus_OfferStatusId(deliverer,4L);

        double sum1=0.0, sum2=0.0;

        for (DelivererOffer df: delivererOffers) {
            sum1+=df.getPrice();
        }

        for (DelivererOffer df: delivererOffers2) {
            sum2+=df.getPrice();
        }

        AllAndThisMonthEarningsDTO allAndThisMonthEarningsDTO = AllAndThisMonthEarningsDTO.builder()
                .sumOfThisMonth(sum1)
                .sumOfAll(sum2)
                .build();

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Draga koleginice Aleksandra obaveštavam Vas da ste uspešno pogodili željeni API. Čestitamo Vam, Vaš Bekend tim! :) ")
                .data(allAndThisMonthEarningsDTO)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getMyJobs(String token) {
        User user = jwtUtil.isTokenValid(token);

        Deliverer deliverer = delivererRepository.getByUser(user);

        List<DelivererOffer> delivererOffersAccepted = delivererOffersRepository.getDelivererOfferByDelivererAndStatus_OfferStatusId(deliverer,2L);

        List<MyJobsResponse> myJobs = new ArrayList<>();

        for (DelivererOffer delivererOf : delivererOffersAccepted) {
            double totalPrice = 0;
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.getPurchaseOrderByOrderId(delivererOf.getOrder());

            for(PurchaseOrder pu : purchaseOrders){
                totalPrice += pu.getProduct().getPrice()*pu.getQuantity();
            }

            Date date = delivererOf.getOrder().getOrderDate();
            String orderDate = null;
            if(date != null) {
                LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
                orderDate = localDate.getDayOfMonth() + "/" + localDate.getMonth().getValue() + "/" + localDate.getYear();
            }
            MyJobsResponse myJobsResponse = MyJobsResponse.builder()
                    .offerId(delivererOf.getOfferId())
                    .offerStatusId(delivererOf.getStatus().getOfferStatusId())
                    .buyerName(delivererOf.getOrder().getBuyer().getName())
                    .buyerSurname(delivererOf.getOrder().getBuyer().getSurname())
                    .buyerUsername(delivererOf.getOrder().getBuyer().getUsername())
                    .buyerPicture(fileSystem.getImageInBytes(String.valueOf(delivererOf.getOrder().getBuyer().getUserId()), ImageType.USER))
                    .orderId(delivererOf.getOrder().getId())
                    .orderDate(orderDate)
                    .purchasePrice(totalPrice)
                    .delivererPrice(delivererOf.getPrice())
                    .build();

            myJobs.add(myJobsResponse);
        }

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .data(myJobs).build(), HttpStatus.OK);
    }

    private boolean isEmailAlreadyTaken(String email) {
        return userRepository.findByEmail(email) != null ? true : false;
    }

    private boolean isUsernameAlreadyTaken(String username) {
        return userRepository.findByUsernameCustom(username) != null ? true : false;
    }
}
