package server.server.dtos.request;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicensesRequest {
    HashMap<String, Boolean> categories;
}
