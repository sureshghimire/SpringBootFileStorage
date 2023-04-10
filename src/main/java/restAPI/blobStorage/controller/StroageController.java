package restAPI.blobStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restAPI.blobStorage.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/blob")
public class StroageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
       String uploadImage = storageService.uploadImage(file);
       return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadImage(@PathVariable  String filename){
        byte[] imageData = storageService.downloadImage(filename);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
               // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment+"+ filename) // this will make the file download on call
                        .body(imageData);

    }

    @GetMapping("/downloadYaml")
    public ResponseEntity<?> downloadYaml(@RequestParam  Long id){
        byte [] dataBytes = storageService.downloadYaml(id);
        ByteArrayResource resource = new ByteArrayResource(dataBytes);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("plain/text")).
                body(resource);

    }

    @PostMapping ("/saveYaml")
    public Object saveAsYaml( @RequestBody String jsonString){
        return storageService.saveAsYaml(jsonString);
    }

    @GetMapping("/getYaml")
    public String getYamlString(@RequestParam  Long id){
        try {
            return storageService.getSavedYaml(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
