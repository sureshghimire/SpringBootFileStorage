package restAPI.blobStorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restAPI.blobStorage.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class StroageController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
       String uploadImage = storageService.uploadImage(file);
       return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadImage(@PathVariable  String filename){
        byte[] imageData = storageService.downloadImage(filename);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("plain/text")).
                body(imageData);

    }

    @PostMapping ("/testName")
    public Object test( @RequestBody String jsonString){
        return storageService.saveAsYaml(jsonString);
    }
}
