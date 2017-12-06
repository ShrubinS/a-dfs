package service;

import dto.Response;
import dto.request.FileIdRequest;

public interface LockService {
    Response createLock(FileIdRequest fileIdRequest) throws Exception;
}
