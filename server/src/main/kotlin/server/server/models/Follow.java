package server.server.models;

import jakarta.persistence.*;
import lombok.*;
import server.server.models.compositeKeys.FollowKey;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follow {

    @EmbeddedId
    private FollowKey followKey;

    @ManyToOne
    @JoinColumn(name="user_id",insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="seller_id",insertable = false, updatable = false)
    private Seller seller;

}
