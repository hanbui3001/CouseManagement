package courseProject.fullSV.repository;

import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CourseRepo extends JpaRepository<Course, String> {
    @Query("select u from Course u left join u.teacher r where u.id = :id and r is null")
    Optional<Course> findByIdWithNull(@Param("id") String id);
    @Query("select u.teacher from Course u where u.id = :id")
    Optional<Users> findTeacherByCourseId(@Param("id") String id);

}
