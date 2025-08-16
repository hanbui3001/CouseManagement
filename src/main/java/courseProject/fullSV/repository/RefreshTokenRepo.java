package courseProject.fullSV.repository;

import courseProject.fullSV.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {
    void deleteByUsername(String username);
    @Query("select u.username from RefreshToken u where u.id = :id")
    String findUserById(@Param("id") String id);
    boolean existsByUsername(String username);
    @Query("select u.expiratedTime from RefreshToken u where u.username = :username")
    List<Date> findByExpired(@Param("username") String username);
}
