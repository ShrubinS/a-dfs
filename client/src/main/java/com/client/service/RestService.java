package com.client.service;


import com.client.dto.FileInfo;
import com.client.dto.FileServerInfo;
import com.client.dto.FileUploadResponse;
import com.client.dto.LockInfo;
import com.sun.net.httpserver.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.FloatBuffer;
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

    public FileServerInfo getFileServer(String fileName, String nameServerInfo) {
        FileServerInfo fileServerInfo = restTemplate.getForObject(nameServerInfo, FileServerInfo.class);
        log.info(fileServerInfo.toString());
        return fileServerInfo;
    }

    public LockInfo getLock(String fileName, String lockserverInfo) {
        LockInfo lockInfo = restTemplate.getForObject(lockserverInfo, LockInfo.class);
        log.info(lockInfo.toString());
        return lockInfo;
    }

    public FileUploadResponse postFile(String localFileName, String fileServerInfo) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        String localTempDir = environment.getProperty("config.localtempdir");
        parameters.add("file", new FileSystemResource(localTempDir + localFileName));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Accept", "text/plain");

        FileUploadResponse result = restTemplate.postForObject(
                fileServerInfo,
                new HttpEntity<MultiValueMap<String, Object>>(parameters, headers),
                FileUploadResponse.class);
        log.info(result.toString());
        return result;
    }

    public FileInfo getFile(String fileName, String fileServerInfo) throws IOException{
        String url = fileServerInfo + fileName;
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);

        byte[] fileBytes = responseEntity.getBody();

        cacheService.writeFile(fileName, fileBytes);

        List<String> lastModified = responseEntity.getHeaders().get("Last-Modified");
        FileInfo fileInfo = new FileInfo();
        fileInfo.setLastModified(Long.parseLong(lastModified.get(0)));
        fileInfo.setLocalFileName(fileName);

        return fileInfo;
    }

    public String getFileHash(String fileName, String fileServerInfo) {
        String url = fileServerInfo + fileName;
        ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity()
    }
}
