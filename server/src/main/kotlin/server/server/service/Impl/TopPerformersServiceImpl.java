package server.server.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.server.dtos.response.TopProductsMonth;
import server.server.dtos.response.TopSellersMonth;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.models.Product;
import server.server.models.Seller;
import server.server.repository.*;
import server.server.service.TopPerformersService;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopPerformersServiceImpl implements TopPerformersService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FileSystemUtil fileSystem;
    @Autowired
    private ProductCommentRepository productCommentRepository;
    @Autowired
    private FollowRepository followRepository;
    @Override
    public List<TopSellersMonth> getTop3SellersOfTheMonth() {
        List<Object[]> list = orderRepository.getTop3SellersOfTheMonth();
        List<TopSellersMonth> topSellersMonthList = new ArrayList<>();
        for (Object o[] : list) {
            Long sellerId = (Long) o[0];
            Long numberOfOrders = (Long) o[1];
            Long numberOfProducts = productRepository.getNumberOfProductsBySellerId(sellerId);

            Seller seller = sellerRepository.findById(sellerId).get();

            TopSellersMonth topSellersMonth = TopSellersMonth.builder()
                    .sellerId(sellerId)
                    .name(seller.getUser().getName())
                    .surname(seller.getUser().getSurname())
                    .username(seller.getUser().getUsername())
                    .picture(fileSystem.getImageInBytes(String.valueOf(seller.getUser().getUserId()), ImageType.USER))
                    .latitude(seller.getLatitude())
                    .longitude(seller.getLongitude())
                    //.numberOfOrders(numberOfOrders)
                    .numberOfFollowers(followRepository.getNumberOfFollowersBySellerId(sellerId))
                    .numberOdProducts(numberOfProducts)
                    .build();

            topSellersMonthList.add(topSellersMonth);
        }

        return topSellersMonthList;
    }

    @Override
    public List<TopProductsMonth> getTop3ProductsOfTheMonth() {
        List<Long> list = orderRepository.getTop3ProductsOfTheMonth();
        List<TopProductsMonth> topProductsMonthList = new ArrayList<>();
        for (Long l: list) {
            Long productId = l;

            Product product = productRepository.findById(productId).get();
            Double averageGrade = productCommentRepository.getAverageGradeByProductId(productId);

            TopProductsMonth topProductsMonth = TopProductsMonth.builder()
                    .productId(productId)
                    .categoryId(product.getCategory().getCategoryId())
                    .productName(product.getProductName())
                    .productPicture(fileSystem.getImageInBytes(String.valueOf(productId), ImageType.PRODUCT))
                    .sellerUsername(product.getSeller().getUser().getUsername())
                    .longitude(product.getSeller().getLongitude())
                    .latitude(product.getSeller().getLatitude())
                    .averageGrade(averageGrade)
                    .build();

            topProductsMonthList.add(topProductsMonth);
        }

        return topProductsMonthList;
    }
}
