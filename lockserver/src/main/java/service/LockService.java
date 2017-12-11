package service;

import dto.Response;
import dto.request.FileIdRequest;

import javax.transaction.Transactional;

public interface LockService {
    Response createLock(FileIdRequest fileIdRequest);

    @Transactional
    Response deleteLock(FileIdRequest fileIdRequest);
}
