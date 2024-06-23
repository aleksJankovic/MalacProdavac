package server.server.dtos.request.simulator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddingNewProductRequestSimulator {
    String product_name;
    long category_id;
    long measurement_id;
    long seller_id;
    byte[] picture;
    long price;
    String description;
    String measurement_value;
}
