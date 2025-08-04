package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.SubjectRequest;
import courseProject.fullSV.dto.response.SubjectResponse;
import courseProject.fullSV.models.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject toSubject(SubjectRequest request);
    SubjectResponse toSubjectResponse(Subject subject);
}
