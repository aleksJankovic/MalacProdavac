package server.server.generalResponses;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private int code;
    private String status;
    private boolean success;
    private String message;
    private Object data;
}
