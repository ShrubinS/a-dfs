package service;

import exception.LockServerFileExistsException;
import model.FileLockMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.FileLockMapRepository;

import java.util.List;

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
            throw new LockServerFileExistsException("file already exists");
        }
        FileLockMap fileLockMap = new FileLockMap();
        fileLockMap.setFileName(fileName);
        fileLockMap.setLocked(false);
        fileLockMapRepository.save(fileLockMap);
        return "success";
    }
}
