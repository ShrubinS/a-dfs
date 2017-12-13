package com.scfss.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.scfss.dto.FileListingResponse;
import com.scfss.dto.FileUploadResponse;
import com.scfss.service.FileHashService;
import com.scfss.service.NotifyLockServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.scfss.storage.StorageFileNotFoundException;
import com.scfss.storage.StorageService;


@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final StorageService storageService;
    private final FileHashService fileHashService;
    private final NotifyLockServerService notifyLockServerService;

    @Autowired
    public FileController(FileHashService fileHashService, StorageService storageService, NotifyLockServerService notifyLockServerService) {
        this.fileHashService = fileHashService;
        this.storageService = storageService;
        this.notifyLockServerService = notifyLockServerService;
    }

//    @RequestMapping(value = "/connect", method = RequestMethod.GET)
//    public ConnectResponse connect() {
//        return connectService.connect();
//    }

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

    @GetMapping("/files-hash")
    public ResponseEntity<String> getHash(@RequestParam("filename") String filename) throws IOException{
        String filePath = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());
        Resource file = storageService.loadAsResource(filePath);
        String fileHash = fileHashService.getMD5Hash(filePath);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("File-Hash", fileHash);
        httpHeaders.set("Modified-Last", String.valueOf(file.lastModified()));

        log.info("getting hash");

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "details; filename=\"" + filePath + "\"").headers(httpHeaders).body("");
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException{

        Resource file = storageService.loadAsResource(filename);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLastModified(file.lastModified());

        log.info("serving file for" + filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").headers(httpHeaders).body(file);
    }

    @PostMapping("/upload")
    public FileUploadResponse handleFileUpload(@RequestParam("file") MultipartFile file) {

        String message = "file " + file.getOriginalFilename() + " uploaded successfully";
        try {
            storageService.store(file);
            fileHashService.createHashForDB(file);
            notifyLockServerService.notifyFileCreated(file.getOriginalFilename());
        } catch (Exception e) {
            message = e.getMessage();
        }
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setMessage(message);

        log.info("uploading file " + file.getOriginalFilename());

        return fileUploadResponse;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
