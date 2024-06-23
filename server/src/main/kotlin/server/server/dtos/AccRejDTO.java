package server.server.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccRejDTO {
    int accepted;
    int rejected;
    boolean owner;
}
