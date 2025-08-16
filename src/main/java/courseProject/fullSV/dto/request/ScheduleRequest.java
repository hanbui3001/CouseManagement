package courseProject.fullSV.dto.request;

import courseProject.fullSV.enums.Days;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRequest {
    Days day;
    LocalTime timeStart;
    LocalTime timeEnd;
    LocalDate dayStart;
    LocalDate dayEnd;
}
