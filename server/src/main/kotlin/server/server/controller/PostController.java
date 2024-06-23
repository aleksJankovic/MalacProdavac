package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.dtos.request.PostCommentRequest;
import server.server.dtos.request.ProductCommentRequest;
import server.server.service.PostService;

@RestController
@RequestMapping
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/postComment")
    public ResponseEntity<?> postCommentForPost(@RequestHeader("Authorization") String token, @RequestBody PostCommentRequest postCommentRequest){
        return postService.postPostComment(token,postCommentRequest);
    }
}
