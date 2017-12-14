package com.client.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CacheService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CacheService.class);

    private final RestTemplate restTemplate;

    @Value("${config.localtempdir}")
    private String LOCAL_DIR;

    @Autowired
    public CacheService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public File getFile(String filename, String serverInfo) throws IOException, NoSuchAlgorithmException, FileNotFoundException{
        String filePath = LOCAL_DIR + filename;
        File file = new File(filePath);
        if (!file.isFile()) {
            /*
                Cache is empty
             */
            log.info("cache empty, will retrieve from server");
            return null;
        }

        byte[] uploadBytes = Files.readAllBytes(Paths.get(filePath));
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(uploadBytes);
        String clientHash = new BigInteger(1, digest).toString(16);
        Long clientModified = file.lastModified();

        String url = serverInfo + "files-hash?filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new FileNotFoundException("File does not exist on server");
        }
        String serverHash = responseEntity.getHeaders().get("File-Hash").get(0);
        Long serverModified = Long.parseLong(responseEntity.getHeaders().get("Modified-Last").get(0));

        if (serverHash.equals(clientHash)) {
            return file;
        } else {
            /*
                Cache is out of date with server. If there are any changes made locally, and lock is still valid,
                commit changes.

                This will not happen in this implementation, as whenever file is written, it's committed to server.
             */
            if (serverModified > clientModified) {
                /*
                    Other commits have happened and client is out of date. Need to get fresh value from server.
                 */
                return null;
            } else {
                return file;
            }
        }

    }

    public void writeFile(String filename, byte[] fileBytes, String mode) throws IOException{
        switch (mode) {
            case "w":
                if (Files.exists(Paths.get(LOCAL_DIR + filename))) {
                    Files.delete(Paths.get(LOCAL_DIR + filename));
                }
                Files.write(Paths.get(LOCAL_DIR + filename), fileBytes, StandardOpenOption.CREATE);
                break;
            case "a":
                Files.write(Paths.get(LOCAL_DIR + filename), fileBytes, StandardOpenOption.APPEND);
                break;
            default:
                throw new UnsupportedOperationException("only a and w allowed");
        }
    }


}
