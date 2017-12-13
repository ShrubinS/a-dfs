package com.fs.repository;

import com.fs.model.FileLockMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileLockMapRepository extends JpaRepository<FileLockMap, String> {

    FileLockMap findFileLockMapByFileName(String fileName);

}
