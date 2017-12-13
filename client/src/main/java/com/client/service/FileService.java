package com.client.service;

import com.client.dto.FileInfo;
import com.client.dto.FileServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@Service
public class FileService {

    private final RestService restService;
    private final CacheService cacheService;
    private final Environment environment;

    private final String NAMESERVER;
    private final String LOCKSERVER;

    @Autowired
    public FileService(RestService restService, CacheService cacheService, Environment environment) {
        this.restService = restService;
        this.cacheService = cacheService;
        this.environment = environment;

        this.NAMESERVER = environment.getProperty("nameserver.address") + ":" + environment.getProperty("nameserver.port");
        this.LOCKSERVER = environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port");
    }

    public void readFile(String fileName) {
        FileServerInfo serverInfo = restService.getFileServer(fileName, NAMESERVER);

        if (serverInfo == null) {
            throw new NullPointerException("file server not found");
        }

        File file = cacheService.

        try {
            FileInfo fileInfo = restService.getFile(serverInfo.getFilePath(), serverInfo.getFileSystemServerInfo());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String fileName, String toWrite) {

    }

    public void appendToFile(String fileName, String toWrite) {

    }

    public void listFiles(String clientWD) {

    }

    public String changeDir(String toDir) {

        return "";
    }
}
