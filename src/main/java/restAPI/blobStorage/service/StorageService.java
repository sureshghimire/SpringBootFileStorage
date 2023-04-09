package restAPI.blobStorage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import restAPI.blobStorage.entity.ImageData;
import restAPI.blobStorage.repository.StorageRepository;
import restAPI.blobStorage.util.ImageUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public Object saveAsYaml( String data) {
        // data = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";

//         data = "{\n" +
//                "  \"name\": \"John\",\n" +
//                "  \"age\": 30,\n" +
//                "  \"address\": {\n" +
//                "    \"street\": \"123 Main St\",\n" +
//                "    \"city\": \"New York\",\n" +
//                "    \"state\": \"NY\",\n" +
//                "    \"zip\": \"10001\"\n" +
//                "  },\n" +
//                "  \"contacts\": [\n" +
//                "    {\n" +
//                "      \"type\": \"email\",\n" +
//                "      \"value\": \"john@example.com\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\": \"phone\",\n" +
//                "      \"value\": \"555-1234\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";


        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        String name = jsonObject.get("name").getAsString();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(data);
            String asString = new YAMLMapper().writeValueAsString(jsonNode);
            System.out.println("As YAML"+asString);

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

    private String convertJsonToYaml(String jsonObject){
        Yaml yaml = new Yaml();
        Object obj = yaml.load(jsonObject);
        return yaml.dump(obj);
    }
}
