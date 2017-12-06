package controller;


import dto.Response;
import dto.request.FileIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.LockService;

@RestController
public class LockController {

    private final LockService lockService;

    @Autowired
    public LockController(LockService lockService) {
        this.lockService = lockService;
    }

    @RequestMapping(value = "/create-lock", method = RequestMethod.POST)
    public Response createLock(@RequestBody FileIdRequest fileIdRequest) {

    }

}
