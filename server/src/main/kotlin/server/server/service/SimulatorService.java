package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.TipForJobRequest;
import server.server.dtos.request.WorkingTimeRequest;
import server.server.dtos.request.simulator.AddingNewProductRequestSimulator;
import server.server.dtos.request.simulator.SimulatorOrderRequest;

public interface SimulatorService {
    ResponseEntity<?> addNewProduct(AddingNewProductRequestSimulator productRequestSimulator);
    ResponseEntity<?> createAnOrder(SimulatorOrderRequest simulatorOrderRequest);
    ResponseEntity<?> createAPost(Long sellerId, String text);
    ResponseEntity<?> likePost(Long postId, Long userId);
    ResponseEntity<?> commentProduct(Long productId, Long userId, int grade, String comment);
    ResponseEntity<?> commentPost(Long postId, Long userId, String comment);
    ResponseEntity<?> simulateSellersWorkingTime(Long sellerId, WorkingTimeRequest workingTimeRequest);
    ResponseEntity<?> sendTipForJob(Long delivererId, TipForJobRequest tipForJobRequest);
}
