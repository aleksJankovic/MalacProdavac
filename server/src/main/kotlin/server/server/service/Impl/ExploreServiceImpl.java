package server.server.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.server.dtos.ExploreProductDTO;
import server.server.dtos.response.ExploreProductResponse;
import server.server.fileSystemImpl.FileSystemUtil;
import server.server.fileSystemImpl.enums.ImageType;
import server.server.fileSystemImpl.service.StorageService;
import server.server.models.Product;
import server.server.repository.ProductRepository;
import server.server.service.ExploreService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExploreServiceImpl implements ExploreService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    FileSystemUtil fileSystem;

    @Override
    public ExploreProductResponse getRandomProducts(List<Long> excludedIds) throws IOException {
        if(excludedIds == null || excludedIds.size() == 0)
            excludedIds.add(-1L);

        List<Product> products = productRepository.getRandom48Products(excludedIds);

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
}
