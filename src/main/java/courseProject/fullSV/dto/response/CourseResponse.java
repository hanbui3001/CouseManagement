package courseProject.fullSV.dto.response;

import courseProject.fullSV.models.Subject;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String name;
    String description;
    //Subject subject;

}
