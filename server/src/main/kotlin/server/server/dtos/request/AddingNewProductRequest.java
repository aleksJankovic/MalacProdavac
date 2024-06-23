package server.server.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddingNewProductRequest {
    String product_name;
    long category_id;
    long measurement_id;
    byte[] picture;
    long price;
    String description;
    String measurement_value;
}
