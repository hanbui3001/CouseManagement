package courseProject.fullSV.repository;

import courseProject.fullSV.models.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<CourseSchedule,String> {
    @Query("select u from CourseSchedule u where u.course.id = :id")
    Optional<CourseSchedule> findByCourseId(@Param("id") String id);
}
