package courseProject.fullSV.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.StudentResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.CourseMapper;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Enrollment;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.CourseRepo;
import courseProject.fullSV.repository.EnrollmentRepo;
import courseProject.fullSV.repository.UserRepo;
import courseProject.fullSV.service.authentication.UserPrincipal;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TeacherService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    EnrollmentRepo enrollmentRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
     UserRepo userRepo;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @PreAuthorize("hasRole('TEACHER')")
    public Page<UserResponse> getAllUsersByCourse(String id, int pageNo, int pageSize){
        if(!courseRepo.existsById(id)) throw new WebException(ErrorCode.COURSE_NOT_FOUND);
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, Sort.by("id").ascending());
        Page<Users> responses = enrollmentRepo.findAllStudentsByCourse(id, pageable);
        return responses.map(users -> userMapper.toUserResponse(users));
    }
    //@PreAuthorize("hasRole('TEACHER')")
    @PreAuthorize("hasRole('TEACHER')")
    public List<CourseResponse> getMyCourses(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String teacherId = principal.getId();
        List<Course> courseList = userRepo.findCourseByTeacherId(teacherId).orElseThrow(() -> new WebException(ErrorCode.TEACHER_NOT_FOUND));
        return courseList.stream().map(course -> courseMapper.toCourseResponse(course)).toList();

    }
    private final String BACKUP_FILE = "/backup/enrollments/json";
    private void backupEnrollmentToJson(Enrollment enrollment){
        try {
            Files.createDirectories(Paths.get(BACKUP_FILE));
            String fileName = BACKUP_FILE + "/" + LocalDate.now() + "/" + "-enrollment.json";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            List<Enrollment> data = new ArrayList<>();
            File file = new File(fileName);
            if(file.exists()){
                data = Arrays.asList(objectMapper.readValue(file, Enrollment[].class));
                data = new ArrayList<>(data);
            }
            data.add(enrollment);
            objectMapper.writeValue(file, data);
            log.warn("da luu data vao json file");
        } catch (IOException e) {
            log.error("loi khi xuat ra json");
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    public StudentResponse changeStatusStudent(String studentId){
        Enrollment enrollment = enrollmentRepo.findById(studentId).orElseThrow(() -> new WebException(ErrorCode.STUDENT_NOT_FOUND));
        enrollment.setActive("CANCEL");
        String key = "Enrollment:Course:" + enrollment.getCourse().getId();
        redisTemplate.opsForHash().put(key, enrollment.getId(), enrollment.getUsers().getId());
        redisTemplate.expire(key, Duration.ofDays(30));
        log.warn("da luu student status cancel vao redis");
        //backupEnrollmentToJson(enrollment);
        enrollmentRepo.deleteById(studentId);
        return StudentResponse.builder()
                .id(enrollment.getId())
                .active(enrollment.getActive())
                .build();
    }
}
