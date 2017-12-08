package com.scfss.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileListingResponse implements Serializable {
    List<String> files;

    public FileListingResponse() {
        files = new ArrayList<>();
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
