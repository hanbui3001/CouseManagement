package courseProject.fullSV.mapper;


import courseProject.fullSV.dto.response.EnrollmentResponse;
import courseProject.fullSV.models.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentResponse toEnrollmentResponse(Enrollment request);
    //SubjectResponse toSubjectResponse(Subject subject);
}
