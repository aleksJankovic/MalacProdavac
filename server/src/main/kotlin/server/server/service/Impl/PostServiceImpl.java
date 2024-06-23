package server.server.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.request.PostCommentRequest;
import server.server.exceptions.AccessDeniedException;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.PostComment;
import server.server.models.User;
import server.server.repository.PostCommentRepository;
import server.server.repository.PostRepository;
import server.server.repository.UserRepository;
import server.server.service.PostService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    PostRepository postRepository;
    @SneakyThrows
    @Override
    public ResponseEntity<?> postPostComment(String token, PostCommentRequest postCommentRequest) {
        User user = jwtUtil.isTokenValid(token);
        List<String> accRole = Arrays.asList("user", "seller", "deliverer");
        if (!accRole.contains(user.getRole().getName().toLowerCase()))
            throw new AccessDeniedException("Access Denied");


        PostComment comment = PostComment.builder()
                .post(postRepository.getPostByPostId(postCommentRequest.getPostId()))
                .dateTime(LocalDateTime.now())
                .text(postCommentRequest.getText())
                .user(user)
                .build();

        postCommentRepository.save(comment);

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .code(200)
                .data(null)
                .message("Post comment added successfully")
                .build();

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
