package server.server.notifications.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenRequest {
    private String token;
}
