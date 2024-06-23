package server.server.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductCommentRequest {
    private Long productId;
    @NotNull
    private String text;
    @NotNull
    private int grade;
}
