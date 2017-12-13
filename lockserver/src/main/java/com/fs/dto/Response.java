package com.fs.dto;

import java.io.Serializable;

public class Response implements Serializable{
    private Boolean acquired;

    public Boolean getAcquired() {
        return acquired;
    }

    public void setAcquired(Boolean acquired) {
        this.acquired = acquired;
    }
}
