package courseProject.fullSV.repository;

import courseProject.fullSV.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, String> {
    Page<Users> findAll(Pageable pageable);
    boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);
    Optional<Users> findById(String id);
}
