package com.fs.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class FileLockMap {

    @Id
    private String fileName;

    private boolean locked;

    private Timestamp timestamp;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
