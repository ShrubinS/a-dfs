package com.scfss.service;

import com.scfss.dto.ConnectResponse;
import com.scfss.model.FileHash;
import com.scfss.repository.FileHashRepository;
import com.scfss.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class FileHashService {

    private final FileHashRepository fileHashRepository;

    @Autowired
    public FileHashService(FileHashRepository fileHashRepository) {
        this.fileHashRepository = fileHashRepository;
    }

    @Transactional
    public void createHashForDB(MultipartFile file) throws Exception {

        byte[] uploadBytes = file.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(uploadBytes);
        String hashString = new BigInteger(1, digest).toString(16);

        FileHash fileHash = new FileHash();
        fileHash.setFileName(file.getOriginalFilename());
        fileHash.setHashValue(hashString);

        fileHashRepository.save(fileHash);
    }

    @Transactional
    public String getMD5Hash(String filename) {
        FileHash fileHash = fileHashRepository.findOne(filename);
        if (fileHash == null) {
            throw new StorageFileNotFoundException("file not found");
        }
        return fileHash.getHashValue();
    }

}
