package com.scfss.repository;

import com.scfss.model.FileHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileHashRepository extends JpaRepository<FileHash, String> {
}
