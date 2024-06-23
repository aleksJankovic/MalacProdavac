package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostCommentDTO {
    private String text;
    private LocalDateTime dateTime;
    private String username;
    private String name;
    private String surname;
    private byte[] picture;
}
