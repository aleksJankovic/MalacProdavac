package server.server.fileSystemImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import server.server.fileSystemImpl.model.CustomMultipartFile;
import server.server.fileSystemImpl.service.StorageService;
import server.server.fileSystemImpl.utilities.FolderUtility;
import server.server.fileSystemImpl.enums.ImageType;

import java.io.IOException;

@Component
public class FileSystemUtil {
    @Autowired
    private StorageService storageService;
    public byte[] getImageInBytes(String identificationString, ImageType imageType){
        Resource resource = storageService.loadImageAsResource(identificationString, imageType);
        try {
            return resource == null ? null : FolderUtility.convertResourceToByteArray(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveImage(String identificationString, byte[] picture, ImageType imageType){
        CustomMultipartFile customMultipartFile = null;
        if(picture != null && picture.length > 0)
            customMultipartFile = new CustomMultipartFile(picture);

        storageService.store(identificationString, customMultipartFile, imageType);
    }
}
