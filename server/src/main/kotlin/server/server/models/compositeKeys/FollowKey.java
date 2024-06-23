package server.server.models.compositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class FollowKey implements Serializable {
    @Column(name="user_id")
    private Long user_id;
    @Column(name="seller_id")
    private Long seller_id;
}
