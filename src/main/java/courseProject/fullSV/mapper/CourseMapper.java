package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.CourseRequest;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.models.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface CourseMapper {
    @Mapping(target = "courseSchedule", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Course toCourse(CourseRequest request);
    CourseResponse toCourseResponse(Course course);
}
