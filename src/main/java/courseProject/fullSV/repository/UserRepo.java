package courseProject.fullSV.repository;

import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, String> {
    Page<Users> findAll(Pageable pageable);
    boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);
    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.courseList where u.id = :id")
    Optional<Users> findByIdWithCourse(@Param("id") String id);
    @Query("select u from Users u left join  u.role r where r.name = :name")
    Page<Users> findAllTeachers(@Param("name") String name, Pageable pageable);
    @Query("select u from Users u left join  u.role r where (:name is null or r.name = :name)")
    Page<Users> findAllByRole(@Param("name") String name, Pageable pageable);
    @Query("select u from Users u left join u.role r where r.name = :name")
    Optional<Users> findByIdWithRole(@Param("name") String id);
    @Query("select u.courseList from Users u where u.id = :id")
    Optional<List<Course>> findCourseByTeacherId(@Param("id") String id);
}
