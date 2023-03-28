package restAPI.blobStorage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import restAPI.blobStorage.entity.ImageData;
import restAPI.blobStorage.repository.StorageRepository;
import restAPI.blobStorage.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public String uploadImage (MultipartFile file ) throws IOException {
        ImageData imageData = storageRepository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData !=null) {
            return "file uploded " + file.getOriginalFilename();
        }else
            return  null;
    }

    public byte[] downloadImage(String filename){
        Optional<ImageData> dbImageData = storageRepository.findByName(filename);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return  images;
    }
}
