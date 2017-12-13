package com.client.service;

import com.client.dto.FileInfo;
import com.client.dto.FileServerInfo;
import com.client.dto.FileUploadResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.ServerError;
import java.rmi.UnexpectedException;
import java.util.logging.Logger;

@Service
public class FileService {

    private final RestService restService;
    private final CacheService cacheService;
    private final Environment environment;

    private final String NAMESERVER;
    private final String LOCKSERVER;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    public FileService(RestService restService, CacheService cacheService, Environment environment) {
        this.restService = restService;
        this.cacheService = cacheService;
        this.environment = environment;

        this.NAMESERVER = this.environment.getProperty("nameserver.address") + ":" + environment.getProperty("nameserver.port");
        this.LOCKSERVER = this.environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port");
    }

    public File readFile(String fileName) throws Exception{
        FileServerInfo serverInfo = restService.getFileServer(fileName, NAMESERVER);
        if (serverInfo == null) {
            throw new Exception("file server not found");
        }
        File file = cacheService.getFile(fileName, serverInfo.getFileSystemServerInfo());

        /*
            Cache miss
        */
        if (file == null) {
            FileInfo fileInfo = restService.getFile(serverInfo.getFilePath(), serverInfo.getFileSystemServerInfo());
            log.info(fileInfo.getLocalFileName());

            /*
                File should now be present in cache
            */
            file = cacheService.getFile(fileName, serverInfo.getFileSystemServerInfo());

            if (file == null) {
                throw new Exception("error in caching");
            }
        }
        return file;
    }

    public void writeFile(String fileName, String toWrite, String mode) throws Exception{
        cacheService.writeFile(fileName, toWrite.getBytes(), mode);
        FileServerInfo fileServerInfo = restService.getFileServer(fileName, NAMESERVER);
        if (fileServerInfo == null) {
            throw new Exception("file server not found");
        }
        FileUploadResponse fileUploadResponse = restService.postFile(fileName, fileServerInfo.getFileSystemServerInfo());
        log.info(fileUploadResponse.getMessage());
    }

    public void appendToFile(String fileName, String toWrite) throws Exception{
        this.readFile(fileName);
        this.writeFile(fileName, toWrite, "a");
    }

    public void listFiles(String clientWD) {
        
    }

    public String changeDir(String toDir) {

        return "";
    }
}
