package com.scfss.dto;

import java.io.Serializable;

public class FileUploadResponse implements Serializable{
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
