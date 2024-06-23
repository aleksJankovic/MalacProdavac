package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
public class SellerDTO {
    private Long seller_id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private byte[] picture;
    private String pib;
    private String adress;
    private double longitude;
    private double latitude;
    private boolean isFollowed;
    private int numberOfPosts;
    private int numberOfFollowers;
    private double avgGrade;
}
