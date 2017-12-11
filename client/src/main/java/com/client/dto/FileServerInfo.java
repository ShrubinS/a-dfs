package com.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FileServerInfo {

    private String fileSystemServerInfo;
    private String filePath;

    public String getFileSystemServerInfo() {
        return fileSystemServerInfo;
    }

    public void setFileSystemServerInfo(String fileSystemServerInfo) {
        this.fileSystemServerInfo = fileSystemServerInfo;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}