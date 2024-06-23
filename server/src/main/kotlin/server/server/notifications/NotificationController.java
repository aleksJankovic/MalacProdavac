package server.server.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.notifications.dtos.FCMTokenRequest;
import server.server.notifications.service.FirebaseMessagingService;

@RestController
@RequestMapping
public class NotificationController {

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @PostMapping
    private String sendNotificationByToken(@RequestBody NotificationMessage notificationMessage){
        return firebaseMessagingService.sendNotificationByToken(notificationMessage);
    }

    @PostMapping("/setFCM")
    private ResponseEntity<?> setFCMToken(@RequestHeader("Authorization")  String token, @RequestBody FCMTokenRequest fcmToken){
        return firebaseMessagingService.setFCMToken(token, fcmToken);
    }

}
