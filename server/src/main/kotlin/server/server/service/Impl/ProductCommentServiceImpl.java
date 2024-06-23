package server.server.service.Impl;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.CommentDTO;
import server.server.dtos.request.ProductCommentRequest;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.ProductComment;
import server.server.models.User;
import server.server.repository.ProductCommentRepository;
import server.server.repository.ProductRepository;
import server.server.repository.UserRepository;
import server.server.service.ProductCommentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductCommentServiceImpl implements ProductCommentService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCommentRepository productCommentRepository;
    @Autowired
    FileSystemUtil fileSystem;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;
    @SneakyThrows
    @Override
    public ResponseEntity<?> getProductCommentsByProductId(Long productId) {
        List<ProductComment> productComments = productCommentRepository.findByProduct_ProductId(productId);

        List<CommentDTO> comments = new ArrayList<>();
        for (ProductComment p : productComments) {
            CommentDTO commentDTO = CommentDTO.builder()
                    .date(p.getDate())
                    .grade(p.getGrade())
                    .text(p.getText())
                    .name(p.getUser().getName())
                    .surname(p.getUser().getSurname())
                    .username(p.getUser().getUsername())
                    .picture(fileSystem.getImageInBytes(String.valueOf(p.getUser().getUserId()), ImageType.USER))
                    .build();

            comments.add(commentDTO);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> postProductComment(String token, ProductCommentRequest productCommentRequest) {
        User user = jwtUtil.isTokenValid(token);

        ProductComment comment = ProductComment.builder()
                .date(new Date())
                .text(productCommentRequest.getText())
                .grade(productCommentRequest.getGrade())
                .user(user)
                .product(productRepository.getByProductId(productCommentRequest.getProductId()))
                .build();

        productCommentRepository.save(comment);

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .status("OK")
                .code(200)
                .data(null)
                .message("Product comment added successfully")
                .build();

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

}
