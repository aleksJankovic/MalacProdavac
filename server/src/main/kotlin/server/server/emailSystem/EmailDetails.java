package server.server.emailSystem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import server.server.emailSystem.enums.EmailType;
import server.server.dtos.response.InoviceOrderResponse;

@Getter
@Setter
@Builder
public class EmailDetails {
    private EmailType emailType;
    private String recipient;
    private String subject;
    private InoviceOrderResponse inoviceOrderResponse;
    private String attachment;
}
