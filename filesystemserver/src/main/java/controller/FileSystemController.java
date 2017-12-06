package controller;

import dto.ConnectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class FileSystemController {


    @RequestMapping(value = "/connect", method = RequestMethod.GET)
    public ConnectResponse connect() {

    }

}
