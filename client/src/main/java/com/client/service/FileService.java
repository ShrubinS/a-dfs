package com.client.service;

import com.client.dto.FileInfo;
import com.client.dto.FileServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class FileService {

    private final RestService restService;

    @Autowired
    public FileService(RestService restService) {
        this.restService = restService;
    }

    public void readFile(String fileName) {
        FileServerInfo serverInfo = restService.getFileServer(fileName);


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
