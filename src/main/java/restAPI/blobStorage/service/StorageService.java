package restAPI.blobStorage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import restAPI.blobStorage.entity.ImageData;
import restAPI.blobStorage.repository.StorageRepository;
import restAPI.blobStorage.util.ImageUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public Object saveAsYaml( String data) {
        // data = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String name = jsonObject.get("name").getAsString();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(data);

            //----  To Avoid the --- (start of the yaml doc) at the top
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLMapper yamlMapper = new YAMLMapper(yamlFactory);
            yamlMapper.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER,false);

            //--------------------------------

            //String asString = new YAMLMapper().writeValueAsString(jsonNode); // avoid avoid and use this line if you don't want to configure
            String asString = yamlMapper.writeValueAsString(jsonNode);

            byte [] dataBytes  = asString.getBytes();
            ImageData entityToSave = storageRepository.save(ImageData.builder()
                    .name(name)
                    .imageData(dataBytes)
                    .type("text/plain")
                    .build()
            );
            return name;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

//    public String getYaml(Long id) throws IOException {
//        Optional<ImageData> entity = storageRepository.findById(id);
//        if (entity.isPresent()){
//            byte[] yamlBytes = entity.get().getImageData();
//            if (yamlBytes !=null){
//
//
//                //convert byte[] to yaml string
//                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//                String yamlString = objectMapper.readValue(yamlBytes, String.class);
//                return  yamlString;
//            }else {
//                throw new RuntimeException("Yaml data is null");
//            }
//        }else {
//            throw new IOException("Image Data does not exists");
//        }
//    }

    public String getSavedYaml(Long id) throws IOException {
       Optional<ImageData> entity = storageRepository.findById(id);
       if (entity.isPresent()){
           byte[] yamlBytes = entity.get().getImageData();
           if (yamlBytes !=null){
               return new String(yamlBytes, StandardCharsets.UTF_8);
           }else {
               throw new RuntimeException("Yaml data is null");
           }
       }else {
           throw new IOException("Image Data does not exists");
       }

    }



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

    public byte [] downloadYaml(Long id){
        Optional<ImageData> yamlData = storageRepository.findById(id);
        if (yamlData.isPresent()){
            byte []yamlBytes  = yamlData.get().getImageData();
            if (yamlBytes !=null){
               return yamlBytes;
            }else
                try {
                    throw  new Exception("File does not contain data");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }else
            try {
                throw new Exception("Entity does not exists");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    private String convertJsonToYaml(String jsonObject){
        Yaml yaml = new Yaml();
        Object obj = yaml.load(jsonObject);
        return yaml.dump(obj);
    }



}
