package com.scfss.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import com.scfss.dto.ConnectResponse;
import com.scfss.dto.FileListingResponse;
import com.scfss.dto.FileUploadResponse;
import com.scfss.service.ConnectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.scfss.storage.StorageFileNotFoundException;
import com.scfss.storage.StorageService;


@RestController
public class FileController {

    private final StorageService storageService;
    private final ConnectService connectService;

    @Autowired
    public FileController(ConnectService connectService, StorageService storageService) {
        this.connectService = connectService;
        this.storageService = storageService;
    }

    @RequestMapping(value = "/connect", method = RequestMethod.GET)
    public ConnectResponse connect() {
        return connectService.connect();
    }

    @GetMapping("/list")
    public FileListingResponse listUploadedFiles() throws IOException {

//        model.addAttribute("files", storageService.loadAll().map(
//                path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
//                        "serveFile", path.getFileName().toString()).build().toString())
//                .collect(Collectors.toList()));

        FileListingResponse fileListingResponse = new FileListingResponse();
        storageService.loadAll().forEach(path -> fileListingResponse.getFiles().add(path.getFileName().toString()));
        return fileListingResponse;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/upload")
    public FileUploadResponse handleFileUpload(@RequestParam("file") MultipartFile file) {

        String message = "file " + file.getOriginalFilename() + " uploaded successfully";
        try {
            storageService.store(file);
        } catch (Exception e) {
            message = e.getMessage();
        }
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setMessage(message);

        return fileUploadResponse;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ioe) {
        return ResponseEntity.unprocessableEntity().build();
    }

}
