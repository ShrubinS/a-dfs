package com.fs.service;

import com.fs.exception.LockServerFileExistsException;
import com.fs.model.FileLockMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fs.repository.FileLockMapRepository;

@Service
public class FileService {

    private final FileLockMapRepository fileLockMapRepository;

    @Autowired
    public FileService(FileLockMapRepository fileLockMapRepository) {
        this.fileLockMapRepository = fileLockMapRepository;
    }

    public String addFile(String fileName) {
        FileLockMap fileLockMapCheck = fileLockMapRepository.findFileLockMapByFileName(fileName);
        if (fileLockMapCheck != null) {
            return "file already present";
        }
        FileLockMap fileLockMap = new FileLockMap();
        fileLockMap.setFileName(fileName);
        fileLockMap.setLocked(false);
        fileLockMapRepository.save(fileLockMap);
        return "success";
    }
}
