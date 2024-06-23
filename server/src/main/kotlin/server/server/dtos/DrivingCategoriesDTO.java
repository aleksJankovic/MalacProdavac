package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@Builder
public class DrivingCategoriesDTO {
    HashMap<String, Boolean> categories;
}
