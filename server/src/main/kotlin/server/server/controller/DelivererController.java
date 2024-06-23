package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.DelivererUpdateRequest;
import server.server.dtos.request.DrivingLicensesRequest;
import server.server.dtos.request.TipForJobRequest;
import server.server.service.DelivererService;

import java.util.HashMap;

@RestController
@RequestMapping
public class DelivererController {

     @Autowired
    DelivererService delivererService;

    @GetMapping("/delivererPersonalInformation")
    public ResponseEntity<?> delivererPersonalInformation(
            @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(value = "id", required = false) Long id
    ){
        return delivererService.delivererPersonalInformation(token, id);
    }

    @PostMapping("/updateDelivererPersonalInformation")
    public ResponseEntity<?> updatePersonalInformation(@RequestHeader("Authorization") String token,
                                                       @RequestBody DelivererUpdateRequest delivererUpdateRequest){
        return delivererService.updateDelivererInformation(token,delivererUpdateRequest);
    }

    @GetMapping("/deliverer/get-available-jobs")
    public ResponseEntity<?> getListOfJobs(@RequestHeader("Authorization") String token){
        return delivererService.getListOfJobs(token);
    }

    @PostMapping("/deliverer/send-job-offer")
    public ResponseEntity<?> sendTipForJob(@RequestHeader("Authorization") String token, @RequestBody TipForJobRequest tipForJobRequest){
        return delivererService.sendTipForJob(token,tipForJobRequest);
    }

    @GetMapping("/deliverer/driving-licenses")
    public ResponseEntity<?> getDrivingCategories(@RequestHeader(value = "Authorization", required = false) String token,  @RequestParam(value = "id", required = false) Long id) {
        return delivererService.getDrivingCategoriesByUser(token, id);
    }

    @PostMapping("/deliverer/update-driving-licenses")
    public ResponseEntity<?> updateDrivingCategories(@RequestHeader("Authorization") String token, @RequestBody HashMap<String,Boolean> drivingLicensesRequest) {
        return delivererService.updateDrivingCategories(token, drivingLicensesRequest);
    }

    @GetMapping("/performance-analytics/grade-percentage")
    public ResponseEntity<?> getDeliverersPerformanceAnalyticsGradePercentage(@RequestHeader("Authorization") String token,
                                                                              @RequestParam Long delivererId){
        return delivererService.getDeliverersPerformanceAnalyticsGradePercentage(token, delivererId);
    }

    @GetMapping("/accepted-rejected-offers")
    public ResponseEntity<?> getAcceptedAndRejectedOffers( @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(value = "id", required = false) Long id){
        return delivererService.getAcceptedAndRejectedOffers(token, id);
    }

    @GetMapping("/total-earnings")
    public ResponseEntity<?> getTotalEarningsForCurrentMonth(@RequestHeader("Authorization") String token) {
        return delivererService.calculateTotalEarningsForCurrentMonth(token);
    }

    @GetMapping("/myJobs")
    public ResponseEntity<?> getAllMyJobs(@RequestHeader("Authorization") String token){
        return delivererService.getMyJobs(token);
    }
}

