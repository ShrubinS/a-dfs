package com.fs.service;

import com.fs.dto.Response;
import com.fs.dto.request.FileIdRequest;

import javax.transaction.Transactional;

public interface LockService {
    Response createLock(FileIdRequest fileIdRequest);

    @Transactional
    Response deleteLock(FileIdRequest fileIdRequest);
}
