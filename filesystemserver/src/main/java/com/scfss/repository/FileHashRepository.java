package com.scfss.repository;

import com.scfss.model.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileHashRepository extends JpaRepository<FileHash, String> {

//    @Query("select fh from FileHash fh where fh.fileName = :filename")
//    FileHash getByFileName(@Param("filename") String filename);


}
