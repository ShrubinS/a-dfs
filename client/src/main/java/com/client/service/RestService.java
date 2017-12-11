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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RestService {

    private static final Logger log = LoggerFactory.getLogger(RestService.class);

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Autowired
    public RestService(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    public FileServerInfo getFileServer(String fileName) {
        String nameServerInfo = environment.getProperty("nameserver.address") + ":" + environment.getProperty("nameserver.port");
        FileServerInfo fileServerInfo = restTemplate.getForObject(nameServerInfo, FileServerInfo.class);
        log.info(fileServerInfo.toString());
        return fileServerInfo;
    }

    public LockInfo getLock(String fileName) {
        String lockserverInfo = environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port");
        LockInfo lockInfo = restTemplate.getForObject(lockserverInfo, LockInfo.class);
        log.info(lockInfo.toString());
        return lockInfo;
    }

    public FileUploadResponse postFile(String localFileName, String serverInfo) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        String localTempDir = environment.getProperty("localtempdir");
        parameters.add("file", new FileSystemResource(localTempDir + localFileName));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        headers.set("Accept", "text/plain");

        FileUploadResponse result = restTemplate.postForObject(
                serverInfo,
                new HttpEntity<MultiValueMap<String, Object>>(parameters, headers),
                FileUploadResponse.class);
        log.info(result.toString());
        return result;
    }

    public FileInfo getFile(String fileName, String serverInfo) throws IOException{
        String url = serverInfo + fileName;
        String localTempDir = environment.getProperty("localtempdir");


        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);

        byte[] fileBytes = responseEntity.getBody();
        Files.write(Paths.get(localTempDir + fileName), fileBytes);

        List<String> lastModified = responseEntity.getHeaders().get("Last-Modified");
        FileInfo fileInfo = new FileInfo();
        fileInfo.setLastModified(lastModified.get(0));
        fileInfo.setLocalFileName(fileName);

        return fileInfo;
    }
}
