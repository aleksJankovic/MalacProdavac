package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class PostDTO {
    private Long id;
    private String usernameSeller;
    private LocalDateTime dateTime;
    private String text;
    private int likesNumber;
    private int commentsNumber;
    private boolean likedPost;
    boolean owner;
}
