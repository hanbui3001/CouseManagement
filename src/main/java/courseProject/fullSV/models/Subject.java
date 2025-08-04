package courseProject.fullSV.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    List<Course> courseList;
    public void addCourse(Course course){
        if(this.courseList == null){
            this.courseList = new ArrayList<>();
        }
        this.courseList.add(course);
        course.setSubject(this);
    }
}
