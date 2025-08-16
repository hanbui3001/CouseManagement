package courseProject.fullSV.repository;

import courseProject.fullSV.models.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepo extends JpaRepository<CourseSchedule,String> {
}
