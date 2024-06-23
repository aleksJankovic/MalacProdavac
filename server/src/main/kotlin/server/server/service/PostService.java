package server.server.service;

import org.springframework.http.ResponseEntity;
import server.server.dtos.request.PostCommentRequest;
import server.server.dtos.request.ProductCommentRequest;

public interface PostService {
    ResponseEntity<?> postPostComment(String token, PostCommentRequest postCommentRequest);
}
