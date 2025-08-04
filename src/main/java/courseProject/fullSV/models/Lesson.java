package courseProject.fullSV.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String description;
    @OneToMany(mappedBy = "lesson",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<LessonMaterial> lessonMaterials;
}
