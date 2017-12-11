package service;

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
        List<FileLockMap> fileLockMapList = fileLockMapRepository.findFileLockMapByFileName(fileName)
        if (fileLockMapList != null && !fileLockMapList.isEmpty()) {
            throw new UnsupportedOperationException("fileName Already exists");
        }
        FileLockMap fileLockMap = new FileLockMap();
        fileLockMap.setFileName(fileName);
        fileLockMap.setLocked(false);
        fileLockMapRepository.save(fileLockMap);
        return "success";
    }
}
