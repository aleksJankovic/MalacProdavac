package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class CommentDTO {
    private Date date;
    private String text;
    private int grade;

    private String name;
    private String surname;
    private String username;

    private byte[] picture;

}
