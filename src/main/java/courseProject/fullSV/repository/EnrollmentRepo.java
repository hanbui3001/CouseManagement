package courseProject.fullSV.repository;

import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Enrollment;
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
public interface EnrollmentRepo extends JpaRepository<Enrollment, String> {
    boolean existsByUsersAndCourse(Users users, Course course);
    @Query("select a from Users a " +
            "join Enrollment b on a = b.users " +
            "join Course c on b.course = c where c.id = :id")
    Page<Users> findAllStudentsByCourse(@Param("id") String id, Pageable pageable);
    @Query("select a.course from Enrollment a where a.users.id = :id")
    List<Course> findCourseByStudentId(@Param("id") String id);
}
