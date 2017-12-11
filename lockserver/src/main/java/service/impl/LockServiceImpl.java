package service.impl;

import dto.Response;
import dto.request.FileIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.LockService;
import service.RedisService;

@Service
public class LockServiceImpl implements LockService {

    @Override
    public Response createLock(FileIdRequest fileIdRequest) throws Exception {
        return null;
    }
}
