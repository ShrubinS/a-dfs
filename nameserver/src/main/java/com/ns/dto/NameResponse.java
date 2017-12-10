package com.ns.dto;

import java.io.Serializable;

public class NameResponse implements Serializable {
    private String fileSystemServerInfo;

    public String getFileSystemServerInfo() {
        return fileSystemServerInfo;
    }

    public void setFileSystemServerInfo(String fileSystemServerInfo) {
        this.fileSystemServerInfo = fileSystemServerInfo;
    }
}
