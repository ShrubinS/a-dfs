package repository;

import model.FileLockMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileLockMapRepository extends JpaRepository<FileLockMap, String> {

    FileLockMap findFileLockMapByFileName(String fileName);

}
