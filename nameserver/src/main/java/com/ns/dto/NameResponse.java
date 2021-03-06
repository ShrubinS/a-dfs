package com.ns.dto;

import java.io.Serializable;

public class NameResponse implements Serializable {
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
