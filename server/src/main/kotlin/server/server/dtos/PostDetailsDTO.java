package server.server.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PostDetailsDTO {
    private String name;
    private String surname;
    private String username;
    private byte[] picture;
    private double longitude;
    private double latitude;
    private LocalDateTime dateTime;
    private String text;
    private int likesNumber;
    private int commentsNumber;
    private List<PostCommentDTO> postCommentDTOList;
    private boolean likedPost;
}
