package controller;


import exception.LockServerFileExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.FileService;

@RestController
public class FileLockMapController {

    private final FileService fileService;

    @Autowired
    public FileLockMapController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/create-file", method = RequestMethod.POST)
    public void createFile(@RequestBody String fileName) {
        fileService.addFile(fileName);
    }

    @ExceptionHandler(LockServerFileExistsException.class)
    public ResponseEntity<?> handleLockServerException(LockServerFileExistsException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
