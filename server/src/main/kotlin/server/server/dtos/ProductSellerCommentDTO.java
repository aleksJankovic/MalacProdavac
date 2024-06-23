package server.server.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProductSellerCommentDTO {
    SellerDTO sellerDTO;
    ProductDTO productDTO;
    List<CommentDTO> productCommentList;
}
