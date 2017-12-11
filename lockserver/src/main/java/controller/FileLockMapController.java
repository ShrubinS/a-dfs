package controller;


import model.FileLockMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileLockMapController {

    @RequestMapping(value = "/create-file", method = RequestMethod.POST)
    public void createFile(@RequestBody String fileName) {

    }
}
