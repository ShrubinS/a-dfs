package com.client.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileIdRequest implements Serializable{
    private List<String> fileIds;

    public FileIdRequest() {
        fileIds = new ArrayList<>();
    }

    public List<String> getFileIds() {
            return fileIds;
        }

    public void setFileIds(List<String> fileIds) {
            this.fileIds = fileIds;
        }
}
