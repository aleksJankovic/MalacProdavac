package server.server.models;


import jakarta.persistence.*;
import lombok.*;
import server.server.models.compositeKeys.LikeKey;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "likes")

public class Like {
    @EmbeddedId
    private LikeKey likeKey;
    @ManyToOne
    @JoinColumn(name="post_id",insertable = false, updatable = false)
    private Post post;
    @ManyToOne
    @JoinColumn(name="user_id",insertable = false, updatable = false)
    private User user;

    private LocalDateTime dateTime;
}
