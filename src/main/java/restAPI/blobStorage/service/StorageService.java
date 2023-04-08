package restAPI.blobStorage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

    public Object saveAsYaml( String data) {
        //String data = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String name = jsonObject.get("name").getAsString();
        ImageData entityToSave = storageRepository.save(ImageData.builder()
                        .name(name)
                        .imageData(data.getBytes())
                        .type("text")
                        .build()
                                );
        return name;
    }

    public String uploadImage (MultipartFile file ) throws IOException {

        String json = "{'name' : 'mkyong'}";

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
