package courseProject.fullSV.models;

import courseProject.fullSV.enums.Days;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "course_schedule")
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id")
    String id;
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    Days days;
    @Column(name = "time_to_start")
    LocalTime timeStart;
    @Column(name = "time_to_end")
    LocalTime timeEnd;
    @Column(name  = "day_to_start")
    LocalDate dayStart;
    @Column(name = "day_to_end")
    LocalDate dayEnd;
    @OneToOne
    @JoinColumn(name = "course_id" )
    Course course;
}
