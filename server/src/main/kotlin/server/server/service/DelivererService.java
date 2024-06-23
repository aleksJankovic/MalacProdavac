package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.DelivererUpdateRequest;
import server.server.dtos.request.TipForJobRequest;

import java.util.HashMap;

public interface DelivererService {

    ResponseEntity<?> delivererPersonalInformation(String token, Long id);

    ResponseEntity<?> updateDelivererInformation(String token, DelivererUpdateRequest delivererUpdateRequest);

    ResponseEntity<?> getListOfJobs(String token);

    ResponseEntity<?> sendTipForJob(String token, TipForJobRequest tipForJobRequest);
    ResponseEntity<?> getDeliveryOffers(String token);
    ResponseEntity<?> changeDeliveryOfferStatus(String token, Long offerId, boolean offerAccepted);
    ResponseEntity<?> confirmOrderReceiving(String token, Long offerId);

    ResponseEntity<?> getDrivingCategoriesByUser(String token, Long id);

    ResponseEntity<?> updateDrivingCategories(String token, HashMap<String,Boolean> drivingLicensesRequest);
    ResponseEntity<?> getDeliverersPerformanceAnalyticsGradePercentage(String token, Long delivererId);

    ResponseEntity<?> getAcceptedAndRejectedOffers(String token, Long id);

    ResponseEntity<?> calculateTotalEarningsForCurrentMonth(String token);

    ResponseEntity<?> getMyJobs(String token);
}
