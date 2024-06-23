package server.server.notifications.service;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.User;
import server.server.notifications.NotificationMessage;
import server.server.notifications.dtos.FCMTokenRequest;
import server.server.repository.NotificationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FirebaseMessagingService {
    @Autowired
    private  FirebaseMessaging firebaseMessaging;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    JwtUtil jwtUtil;

    public String sendNotificationByToken(NotificationMessage notificationMessage){
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();
        Map<String,String> data = new HashMap<>();
        Message message = Message.builder()
                .setToken(notificationMessage.getRecipientToken())
                .setNotification(notification)
                .putAllData(data)
                .build();

        try{
            firebaseMessaging.send(message);
            return "Success Sending Notificatoin";
        } catch (FirebaseMessagingException e){
            e.printStackTrace();
            return "Error Sending Notification";
        }

    }

    public ResponseEntity<?> setFCMToken (String token, FCMTokenRequest fcmToken){
        User user = jwtUtil.isTokenValid(token);
        Optional<server.server.models.Notification> exist = notificationRepository.findById(user.getUserId());
        if(exist.isPresent()){
            exist.get().setRecipientToken(fcmToken.getToken());
            notificationRepository.save(exist.get());
            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("Uspesno setovan FCM!")
                    .build(),HttpStatus.OK);
        }
        else{
            server.server.models.Notification notification = server.server.models.Notification.builder()
                    .id(user.getUserId())
                    .user(user)
                    .recipientToken(fcmToken.getToken())
                    .build();
            notificationRepository.save(notification);

            return new ResponseEntity<>(SuccessResponse.builder()
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .data(null)
                    .message("Uspesno dodat novi FCM!")
                    .build(),HttpStatus.OK);
        }
    }
}
