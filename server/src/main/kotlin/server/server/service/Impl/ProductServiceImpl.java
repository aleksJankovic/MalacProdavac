package server.server.service.Impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.server.dtos.*;
import server.server.dtos.request.AddingNewProductRequest;
import server.server.dtos.response.ExploreProductResponse;
import server.server.exceptions.InvalidProductIdException;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.generalResponses.ErrorResponse;
import server.server.generalResponses.SuccessResponse;
import server.server.jwt.JwtUtil;
import server.server.models.*;
import server.server.repository.*;
import server.server.service.ProductService;

import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductCommentRepository productCommentRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    FileSystemUtil fileSystem;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public ResponseEntity<?> getProductById(String token, Long id) throws IOException {
        Optional<Product> product = productRepository.findById(id);

        if(product.isEmpty()){
            ProductDTO productDTO = null;
            return new ResponseEntity<>(productDTO,HttpStatus.OK);
        }

        Long userId;
        boolean owner = false;
        if(token != null){
            User user = jwtUtil.isTokenValid(token);
            userId = user.getUserId();
            if(userId == product.get().getSeller().getUser().getUserId()) owner = true;
        }

        Double averageGrade = productCommentRepository.getAverageGradeByProductId(id);
        ProductDTO productDTO = ProductDTO.builder()
                .id(id)
                .productName(product.get().getProductName())
                .category(product.get().getCategory().getName())
                .description(product.get().getDescription())
                .sellerName(product.get().getSeller().getUser().getName())
                .price(product.get().getPrice())
                .picture(fileSystem.getImageInBytes(String.valueOf(product.get().getProductId()), ImageType.PRODUCT))
                .measurement(product.get().getMeasurement().getName())
                .category_id(product.get().getCategory().getCategoryId())
                .available(product.get().isAvailable())
                .averageGrade(averageGrade)
                .owner(owner)
                .build();

        SellerDTO sellerDTO = SellerDTO.builder()
                .seller_id(product.get().getSeller().getId())
                .latitude(product.get().getSeller().getLatitude())
                .longitude(product.get().getSeller().getLongitude())
                .pib(product.get().getSeller().getPib())
                .adress(product.get().getSeller().getAddress())
                .surname(product.get().getSeller().getUser().getSurname())
                .name(product.get().getSeller().getUser().getName())
                .username(product.get().getSeller().getUser().getUsername())
                .email(product.get().getSeller().getUser().getEmail())
                .picture(fileSystem.getImageInBytes(String.valueOf(product.get().getSeller().getUser().getUserId()), ImageType.USER))
                .build();


        List<ProductComment> productComments = productCommentRepository.findByProduct_ProductId(id);
        List<CommentDTO> comments = new ArrayList<>();

        if(!productComments.isEmpty()) {

            List<ProductComment> randomComments = getRandomComments(productComments);

            for (ProductComment p : randomComments) {
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
        }

        ProductSellerCommentDTO productSellerCommentDTO = new ProductSellerCommentDTO(sellerDTO,productDTO,comments);

        return new ResponseEntity<>(productSellerCommentDTO,HttpStatus.OK);

    }

    private List<ProductComment> getRandomComments(List<ProductComment> allComments) {
        int numberOfCommentsForProduct = allComments.size();

        int number;
        Date date = new Date();

        if(date.getDay()%2==0){
            number = Math.min(3,numberOfCommentsForProduct);
        }else {
            number = Math.min(5,numberOfCommentsForProduct);
        }

        return allComments.subList(0,number);
    }

    @Override
    public List<Product> getProductByName(String productName) {
        return productRepository.findByProductNameContainingIgnoreCase(productName);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<?> getSellerByProductId(Long productId) {
        Seller seller = productRepository.findSellerByProductId(productId);
        if(seller == null){
            throw new InvalidProductIdException("Ovakav proizvodjac ne postoji");
        }
        return new ResponseEntity<>(SellerDTO.builder()
                .name(seller.getUser().getName())
                .username(seller.getUser().getUsername())
                .surname(seller.getUser().getSurname())
                .email(seller.getUser().getUsername())
                .picture(fileSystem.getImageInBytes(seller.getUser().getUsername(), ImageType.USER))
                .pib(seller.getPib())
                .adress(seller.getAddress())
                .longitude(seller.getLongitude())
                .latitude(seller.getLatitude())
                .build(),HttpStatus.OK);
    }

    @Override
    @SneakyThrows
    public ResponseEntity<?> addNewProduct(String token, AddingNewProductRequest addingNewProductRequest) {
        User user = jwtUtil.isTokenValid(token);

        Product newProduct = Product.builder()
                .productName(addingNewProductRequest.getProduct_name())
                .category(categoryRepository.findById((long)addingNewProductRequest.getCategory_id()).get())
                .seller(sellerRepository.findById(user.getUserId()).get())
                .measurement(measurementRepository.findByMeasurementId((long)(addingNewProductRequest.getMeasurement_id())).get())
                .price(addingNewProductRequest.getPrice())
                .description(addingNewProductRequest.getDescription())
                .available(true)
                .build();

        if(addingNewProductRequest.getMeasurement_value() != null)
            newProduct.setMeasurement_value(addingNewProductRequest.getMeasurement_value());

        Product savedProduct = productRepository.save(newProduct);

        fileSystem.saveImage(String.valueOf(savedProduct.getProductId()), addingNewProductRequest.getPicture(), ImageType.PRODUCT);

        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Product added successfully.")
                .data(savedProduct.getProductId()).build()
                , HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> getProductsByCategory(Long id) {
        List<ProductDTO> productDTOS = new ArrayList<>();
        Category category = categoryRepository.findByCategoryId(id);

        List<Product> products = productRepository.getProductsByCategory(category);

        Collections.shuffle(products);

        int limit = Math.min(products.size(),12);

        List<Product> selectedProducts = products.subList(0,limit);

        for(Product p : selectedProducts){
            ProductDTO productDTO = ProductDTO.builder()
                    .id(p.getProductId())
                    .productName(p.getProductName())
                    .sellerName(p.getSeller().getUser().getUsername())
                    .category(p.getCategory().getName())
                    .measurement(p.getMeasurement().getName())
                    .price(p.getPrice())
                    .description(p.getDescription())
                    .picture(fileSystem.getImageInBytes(String.valueOf(p.getProductId()), ImageType.PRODUCT))
                    .category_id(p.getCategory().getCategoryId())
                    .available(p.isAvailable())
                    .build();

            productDTOS.add(productDTO);
        }

        SuccessResponse successResponse = SuccessResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .data(productDTOS)
                .message("success")
                .status(HttpStatus.OK.name())
                .build();

        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @Override
    public ExploreProductResponse getRandomProductsByCategory(List<Long> excludedIds, Long id) {
        if(excludedIds == null || excludedIds.size() == 0)
            excludedIds.add(-1L);

        Category category = categoryRepository.findByCategoryId(id);
        List<Product> products = productRepository.getRandom9ProductsByCategory(excludedIds,category);

        List<ExploreProductDTO> exploreProductList = new ArrayList<>();

        List<Long> newExcludedIds = new ArrayList<>(excludedIds);
        for (Product product : products) {
            //Mapiranje objekata
            ExploreProductDTO exploreProductDTO = ExploreProductDTO.builder()
                    .categoryId(product.getCategory().getCategoryId())
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .picture(fileSystem.getImageInBytes(String.valueOf(product.getProductId()), ImageType.PRODUCT)).build();
            exploreProductList.add(exploreProductDTO);

            newExcludedIds.add(product.getProductId());
        }

        return ExploreProductResponse.builder()
                .randomProducts(exploreProductList)
                .listOfIDs(newExcludedIds)
                .build();
    }

    @Override
    public ResponseEntity<?> changeProductImage(String token, byte[] image, Long productId) {
        User user = jwtUtil.isTokenValid(token);

        Product product = productRepository.findById(productId).get();
        fileSystem.saveImage(String.valueOf(product.getProductId()), image, ImageType.PRODUCT);

        byte[] changedImage = fileSystem.getImageInBytes(String.valueOf(product.getProductId()), ImageType.PRODUCT);
        return new ResponseEntity<>(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .success(true)
                .message("Product image changed successfully")
                .data(changedImage).build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> deleteProductById(String token, Long productId) {
        User user = jwtUtil.isTokenValid(token);

        Product productToDelete = productRepository.getByProductId(productId);
        if(productToDelete == null)
            return new ResponseEntity<>(ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.name())
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Pokusavate da obrisete proizvod koji ne postoji.")
                    .build(), HttpStatus.NOT_FOUND);
        deleteAllRelatedRecords(productToDelete);
        productRepository.delete(productToDelete);

        return new ResponseEntity<>(SuccessResponse.builder()
                .status(HttpStatus.OK.name())
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Product deleted successfully")
                .success(true)
                .data(null).build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> editProductById(String token, Long productId, AddingNewProductRequest addingNewProductRequest) {
        User user = jwtUtil.isTokenValid(token);

        Product productToEdit = productRepository.getByProductId(productId);
        if(productToEdit == null){
            return new ResponseEntity<>(ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.name())
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Ooops... Looks like the product with id: " + productId + " doesn't exists!")
                    .build(), HttpStatus.NOT_FOUND);
        }

        productToEdit.setProductName(addingNewProductRequest.getProduct_name());
        productToEdit.setMeasurement_value(addingNewProductRequest.getMeasurement_value());
        productToEdit.setMeasurement(measurementRepository.findByMeasurementId(addingNewProductRequest.getMeasurement_id()).get());
        fileSystem.saveImage(String.valueOf(productId), addingNewProductRequest.getPicture(), ImageType.PRODUCT);
        productToEdit.setCategory(categoryRepository.findByCategoryId(addingNewProductRequest.getCategory_id()));
        productToEdit.setPrice(addingNewProductRequest.getPrice());
        productToEdit.setDescription(addingNewProductRequest.getDescription());

        Product savedProduct = productRepository.save(productToEdit);

        ProductDTO productDTO = ProductDTO.builder()
                .id(savedProduct.getProductId())
                .productName(savedProduct.getProductName())
                .sellerName(savedProduct.getSeller().getUser().getUsername())
                .category(savedProduct.getCategory().getName())
                .measurement(savedProduct.getMeasurement().getName())
                .price(savedProduct.getPrice())
                .description(savedProduct.getDescription())
                .picture(fileSystem.getImageInBytes(String.valueOf(savedProduct.getProductId()), ImageType.PRODUCT))
                .category_id(savedProduct.getCategory().getCategoryId())
                .available(savedProduct.isAvailable())
                .build();

        return savedProduct == null ? new ResponseEntity<>(ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Greska prilikom pokusaja izmene proizvoda")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR) : new ResponseEntity<>(SuccessResponse.builder()
                                                                            .status(HttpStatus.OK.name())
                                                                            .code(HttpStatus.OK.value())
                                                                            .success(true)
                                                                            .message("Proizvod uspesno izmenjen")
                                                                            .data(productDTO).build(), HttpStatus.OK) ;


    }

    private void deleteAllRelatedRecords(Product product){
        purchaseOrderRepository.deleteAllByProduct(product);
        productCommentRepository.deleteByProduct(product);
    }

}
