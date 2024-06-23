package server.server.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostCommentRequest {
    private Long postId;
    @NotNull
    private String text;
}
