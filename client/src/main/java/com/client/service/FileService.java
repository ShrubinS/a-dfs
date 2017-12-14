package com.client.service;

import com.client.dto.FileInfo;
import com.client.dto.FileServerInfo;
import com.client.dto.FileUploadResponse;
import com.client.dto.LockInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.InternalFrameUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.ServerError;
import java.rmi.UnexpectedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Service
public class FileService {

    private final RestService restService;
    private final CacheService cacheService;
    private final Environment environment;

    private final String NAMESERVER;
    private final String LOCKSERVER;

    @Value("${lock.max_tries}")
    private String MAX_TRIES;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    public FileService(RestService restService, CacheService cacheService, Environment environment) {
        this.restService = restService;
        this.cacheService = cacheService;
        this.environment = environment;

        this.NAMESERVER = this.environment.getProperty("nameserver.address") + ":" + environment.getProperty("nameserver.port") + "/";
        this.LOCKSERVER = this.environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port") + "/";
    }

    public void readFile(String fileName) throws Exception{
        FileServerInfo serverInfo = restService.getFileServer(fileName, NAMESERVER);
        if (serverInfo == null) {
            throw new Exception("file server not found");
        }
        File file;
        try {
            file = cacheService.getFile(serverInfo.getFilePath(), serverInfo.getFileSystemServerInfo());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        /*
            Cache miss
        */
        if (file == null) {
            try {
                FileInfo fileInfo = restService.getFile(serverInfo.getFilePath(), serverInfo.getFileSystemServerInfo());
                log.info(fileInfo.getLocalFileName());
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                return;
            }

            /*
                File should now be present in cache
            */
            file = cacheService.getFile(serverInfo.getFilePath(), serverInfo.getFileSystemServerInfo());
            if (file == null) {
                throw new Exception("error in caching");
            }
        }
        byte[] text = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        System.out.write(text);
    }

    public void writeFile(String fileName, String toWrite, String mode) throws Exception{

        FileServerInfo serverInfo = restService.getFileServer(fileName, NAMESERVER);
        cacheService.writeFile(serverInfo.getFilePath(), toWrite.getBytes(), mode);
        FileServerInfo fileServerInfo = restService.getFileServer(fileName, NAMESERVER);
        if (fileServerInfo == null) {
            throw new IOException("file server not found");
        }
        writeFileAsync(serverInfo.getFilePath(), fileServerInfo.getFileSystemServerInfo());

    }

    public void appendToFile(String fileName, String toWrite) throws Exception{
        this.readFile(fileName);
        this.writeFile(fileName, toWrite, "a");
    }

    public void listFiles(String clientWorkingDir) {

    }

    public String changeDir(String currentDir, String toDir) {
        String changedDir;
        String addSlash = "";
        if (!toDir.endsWith("/")) {
            addSlash = "/";
        }

        if (toDir.startsWith("/")) {
            /*
                Absolute path
            */
            changedDir = toDir + addSlash;
        } else {
            /*
                Relative path
             */
            changedDir = currentDir + toDir + addSlash;
        }
        return changedDir;
    }

    private void writeFileAsync(String fileName, String fileServerInfo) {
        Future future = CompletableFuture
                .runAsync(() -> {
                    int i = 0;
                    while (restService.getLock(fileName, LOCKSERVER) == null) {
                        try {
                            Thread.sleep(500);
                            System.out.println("Waiting to acquire lock");
                            i++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i == Integer.parseInt(MAX_TRIES)) {
                            throw new RuntimeException("Lock could not be obtained");
                        }
                    }
                    FileUploadResponse fileUploadResponse = restService.postFile(fileName, fileServerInfo + "upload");
                    restService.deleteLock(fileName, LOCKSERVER);
                    log.info("write operation completed for file " + fileName);
                })
                .handle((result, ex) -> "Error: " + ex.getMessage());
    }
}
