package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.AddingNewProductRequest;
import server.server.dtos.request.TipForJobRequest;
import server.server.dtos.request.WorkingTimeRequest;
import server.server.dtos.request.simulator.AddingNewProductRequestSimulator;
import server.server.dtos.request.simulator.SimulatorOrderRequest;
import server.server.service.SimulatorService;

@RestController
@RequestMapping("/simulator")
public class SimulatorController {
    @Autowired
    private SimulatorService simulatorService;
    @PostMapping("/add-product")
    public ResponseEntity<?> addNewProduct(@RequestBody AddingNewProductRequestSimulator productRequestSimulator){
        return simulatorService.addNewProduct(productRequestSimulator);
    }
    @PostMapping("/make-order")
    public ResponseEntity<?> createAnOrder(@RequestBody SimulatorOrderRequest simulatorOrderRequest){
        return simulatorService.createAnOrder(simulatorOrderRequest);
    }

    @PostMapping("/add-post")
    public ResponseEntity<?> addNewPost(@RequestParam Long sellerId, @RequestParam String text){
        return simulatorService.createAPost(sellerId, text);
    }

    @PostMapping("/like-post")
    public ResponseEntity<?> likePost(@RequestParam Long postId, @RequestParam Long userId){
        return simulatorService.likePost(postId, userId);
    }

    @PostMapping("/comment-product")
    public ResponseEntity<?> commentProduct(@RequestParam Long productId,
                                            @RequestParam Long userId,
                                            @RequestParam int grade,
                                            @RequestParam String comment){
        return simulatorService.commentProduct(productId, userId, grade, comment);
    }

    @PostMapping("/comment-post")
    public ResponseEntity<?> commentPost(@RequestParam Long postId,
                                         @RequestParam Long userId,
                                         @RequestParam String comment){
        return simulatorService.commentPost(postId, userId, comment);
    }

    @PostMapping("/seller/set-working-time")
    public ResponseEntity<?> simulateSellersWorkingTime(@RequestParam Long sellerId,
                                                        @RequestBody WorkingTimeRequest workingTimeRequest){
        return simulatorService.simulateSellersWorkingTime(sellerId, workingTimeRequest);
    }

    @PostMapping("/deliverer/send-job-offer")
    public ResponseEntity<?> sendTipForJob(@RequestParam Long delivererId,
                                           @RequestBody TipForJobRequest tipForJobRequest){
        return simulatorService.sendTipForJob(delivererId, tipForJobRequest);
    }
}
