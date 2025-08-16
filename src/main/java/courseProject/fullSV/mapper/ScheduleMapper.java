package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.CourseRequest;
import courseProject.fullSV.dto.request.ScheduleRequest;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.ScheduleResponse;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.CourseSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ScheduleMapper {
    @Mapping(target = "course", ignore = true)
    CourseSchedule toSchedule(ScheduleRequest scheduleRequest);
    //@Mapping(target = "course", ignore = true)
    ScheduleResponse toScheduleResponse(CourseSchedule courseSchedule);
}
