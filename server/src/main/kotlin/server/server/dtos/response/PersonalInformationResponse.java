package server.server.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInformationResponse {
    private boolean isNameChanged;
    private boolean isSurnameChanged;
    private boolean isUsernameChanged;
    private boolean isEmailChanged;
    private String newToken;

}
