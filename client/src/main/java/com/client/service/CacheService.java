package com.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

@Service
public class CacheService {

    private final RestTemplate restTemplate;

    @Value("${config.localtempdir}")
    private String LOCAL_DIR;

    @Autowired
    public CacheService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public File getFile(String filename, String serverInfo) throws Exception{
        String filePath = LOCAL_DIR + filename;
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new Exception("file wasn't found");
        }

        byte[] uploadBytes = Files.readAllBytes(Paths.get(filePath));
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(uploadBytes);
        String clientHash = new BigInteger(1, digest).toString(16);
        Long cliendModified = file.lastModified();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(serverInfo, String.class);
        String serverHash = responseEntity.getHeaders().get("File-Hash").get(0);
        Long serverModified = Long.parseLong(responseEntity.getHeaders().get("Last-Modified").get(0));

        if (serverHash.equals(clientHash)) {
            return file;
        } else {
            /*
                Cache is out of date with server. If there are any changes made locally, and lock is still valid,
                commit changes.

                This will not happen in this implementation, as whenever file is written, it's committed to server
             */
        }

    }

    public void writeFile(String filename, byte[] fileBytes) throws IOException{
        Files.write(Paths.get(LOCAL_DIR + filename), fileBytes);
    }

}
