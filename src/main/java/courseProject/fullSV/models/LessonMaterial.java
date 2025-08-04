package courseProject.fullSV.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lesson_material")
public class LessonMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "file_name")
    String fileName;
    @Column(name = "file_url")
    String fileUrl;
    @Column(name = "file_path")
    String filePath;
    @Column(name = "file_type")
    String fileType;
    @Column(name = "is_external")
    boolean isExternal;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "lesson_id")
    Lesson lesson;
}
