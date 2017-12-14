package com.client.service;


import com.client.dto.*;
import com.sun.net.httpserver.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RestService {

    private static final Logger log = LoggerFactory.getLogger(RestService.class);

    private final RestTemplate restTemplate;
    private final Environment environment;
    private final CacheService cacheService;

    @Autowired
    public RestService(RestTemplate restTemplate, Environment environment, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.environment = environment;
        this.cacheService = cacheService;
    }

    public FileServerInfo getFileServer(String fileName, String nameServerInfo) throws Exception{
        String url = nameServerInfo + "server-info?filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());;
        FileServerInfo fileServerInfo = restTemplate.getForObject(url, FileServerInfo.class);
        log.info("fileserver acquired for" + fileServerInfo.getFilePath());
        return fileServerInfo;
    }

    public LockInfo getLock(String fileName, String lockserverInfo) {
        FileIdRequest fileIdRequest = new FileIdRequest();
        fileIdRequest.getFileIds().add(fileName);
        ResponseEntity<LockInfo> lockInfoResponseEntity= restTemplate.postForEntity(lockserverInfo + "create-lock", fileIdRequest, LockInfo.class);
        if (lockInfoResponseEntity.getStatusCode() == HttpStatus.CONFLICT) {
            return null;
        }
        LockInfo lockInfo = lockInfoResponseEntity.getBody();
        log.info("lock acquired for file " + fileName);
        return lockInfo;
    }

    public LockInfo deleteLock(String fileName, String lockserverInfo) {
        FileIdRequest fileIdRequest = new FileIdRequest();
        fileIdRequest.getFileIds().add(fileName);
        LockInfo lockInfo = restTemplate.postForObject(lockserverInfo + "delete-lock", fileIdRequest, LockInfo.class);
        log.info("lock removed for file " + fileName);
        return lockInfo;
    }

    public FileUploadResponse postFile(String localFileName, String fileServerInfo) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        String localTempDir = environment.getProperty("config.localtempdir");
        parameters.add("file", new FileSystemResource(localTempDir + localFileName));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");

        FileUploadResponse result = null;

        try {
            result = restTemplate.postForObject(
                    fileServerInfo,
                    new HttpEntity<MultiValueMap<String, Object>>(parameters, headers),
                    FileUploadResponse.class);
            log.info(result.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public FileInfo getFile(String fileName, String fileServerInfo) throws IOException, FileNotFoundException{
        String url = fileServerInfo + "files/" + fileName;
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new FileNotFoundException("No such file exists");
        }

        byte[] fileBytes = responseEntity.getBody();

        cacheService.writeFile(fileName, fileBytes, "w");

        List<String> lastModified = responseEntity.getHeaders().get("Last-Modified");
        FileInfo fileInfo = new FileInfo();
        fileInfo.setLastModified(Long.parseLong(lastModified.get(0)));
        fileInfo.setLocalFileName(fileName);

        return fileInfo;
    }

}
