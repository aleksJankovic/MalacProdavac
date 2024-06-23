package server.server.fileSystemImpl.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import server.server.fileSystemImpl.enums.ImageType;


import java.nio.file.Path;

public interface StorageService {
    void store(String identificationString, MultipartFile multipartFile, ImageType imageType);

    Path getFileLocation(String identificationString, ImageType imageType);

    Resource loadImageAsResource(String identificationString, ImageType imageType);
}
