package server.server.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelDTO {
    private Long seller_id;
    private String username;
    private String name;
    private String surname;
    private byte[] picture;
    private String pib;

    private double longitude;
    private double latitude;

    private int numberOfFollowers;
    private int numberOfPosts;
    private int numberOfProducts;
    private boolean isFollowed;

    private double avgGrade;
    private boolean isProfileOwner;

}
