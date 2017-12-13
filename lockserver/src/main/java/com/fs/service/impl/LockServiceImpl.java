package com.fs.service.impl;

import com.fs.dto.Response;
import com.fs.dto.request.FileIdRequest;
import com.fs.exception.LockServerConflictException;
import com.fs.model.FileLockMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fs.repository.FileLockMapRepository;
import com.fs.service.LockService;
import com.fs.util.TimeStampUtil;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LockServiceImpl implements LockService {

    private final FileLockMapRepository fileLockMapRepository;

    @Value("${config.maxtime}")
    private Integer MAX_TIME;

    @Autowired
    public LockServiceImpl(FileLockMapRepository fileLockMapRepository) {
        this.fileLockMapRepository = fileLockMapRepository;
    }

    /*
        Locks which are older than MAX_TIME are considered to have expired, if someone else requests for it

        Below must be synchronized so that create, delete don't have conflicts.
        Also, these must be transactional in nature.
        Both of these are provided by annotating with @Transactional
     */

    @Override
    @Transactional
    public Response createLock(FileIdRequest fileIdRequest) {
        List<FileLockMap> fileLockMap = new ArrayList<>();
        fileIdRequest.getFileIds().forEach(file -> {
            FileLockMap flm = fileLockMapRepository.findFileLockMapByFileName(file);
            if (flm != null) {
                if (flm.isLocked()) {
                    Timestamp now = new Timestamp(new Date().getTime());
                    if (TimeStampUtil.compareTwoTimeStamps(now, flm.getTimestamp()) < MAX_TIME)
                        throw new LockServerConflictException("already locked");
                }
                fileLockMap.add(flm);
            }

        });

        fileLockMap.forEach(lock -> {
            lock.setLocked(true);
            lock.setTimestamp(new Timestamp(System.currentTimeMillis()));
        });
        fileLockMapRepository.save(fileLockMap);

        Response response = new Response();
        response.setAcquired(true);
        return response;
    }

    @Override
    @Transactional
    public Response deleteLock(FileIdRequest fileIdRequest) {
        List<FileLockMap> fileLockMap = new ArrayList<>();
        fileIdRequest.getFileIds().forEach(file -> {
            FileLockMap flm = fileLockMapRepository.findFileLockMapByFileName(file);
            fileLockMap.add(flm);
        });

        fileLockMap.forEach(lock -> lock.setLocked(false));
        fileLockMapRepository.save(fileLockMap);

        Response response = new Response();
        response.setAcquired(false);
        return response;
    }
}
