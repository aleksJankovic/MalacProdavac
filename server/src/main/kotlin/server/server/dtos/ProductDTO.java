package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProductDTO {
    private Long id;
    private String sellerName;
    private String productName;
    private byte[] picture;
    private String description;
    private double price;
    private String category;
    private String measurement;
    private Long category_id;
    private boolean available;
    private Double averageGrade;
    private boolean owner;
}
