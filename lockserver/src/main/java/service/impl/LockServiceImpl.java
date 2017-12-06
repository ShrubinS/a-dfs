package service.impl;

import dto.Response;
import dto.request.FileIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.LockService;
import service.RedisService;

@Service
public class LockServiceImpl implements LockService {

    private final RedisService redisService;

    @Autowired
    public LockServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public synchronized Response createLock(FileIdRequest fileIdRequest) throws Exception {
        fileIdRequest.getFileIds().forEach(fileId -> {
            if (!redisService.hasKey(fileId)) {

            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
