package com.fs.controller;

import com.fs.dto.Response;
import com.fs.dto.request.FileIdRequest;
import com.fs.exception.LockServerConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fs.service.LockService;

@RestController
public class LockController {

    private final LockService lockService;

    @Value("${config.maxtime}")
    private Integer EXPIRES;

    @Autowired
    public LockController(LockService lockService) {
        this.lockService = lockService;
    }

    @RequestMapping(value = "/create-lock", method = RequestMethod.POST)
    public ResponseEntity<Response> createLock(@RequestBody FileIdRequest fileIdRequest) {
        Response response =  lockService.createLock(fileIdRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setExpires(EXPIRES);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete-lock", method = RequestMethod.POST)
    public Response deleteLock(@RequestBody FileIdRequest fileIdRequest) {
        return lockService.deleteLock(fileIdRequest);
    }

    @ExceptionHandler(LockServerConflictException.class)
    public ResponseEntity<?> handleLockServerException(LockServerConflictException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
