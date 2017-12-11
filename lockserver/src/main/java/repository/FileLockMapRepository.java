package repository;

import model.FileLockMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileLockMapRepository extends JpaRepository<FileLockMap, Long> {

    List<FileLockMap> findFileLockMapByFileName(String fileName);
}