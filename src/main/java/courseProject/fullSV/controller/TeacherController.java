package courseProject.fullSV.controller;

import courseProject.fullSV.dto.response.ApiResponse;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.repository.CourseRepo;
import courseProject.fullSV.service.TeacherService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    TeacherService teacherService;
    @Autowired
    CourseRepo courseRepo;
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllStudentByCourse(@RequestParam(value = "course_id") String id,
                                                                                 @RequestParam(value = "pageNo", defaultValue = "1") int no,
                                                                                 @RequestParam(value = "pageSize", defaultValue = "5")int size){
        Course course = courseRepo.findById(id).orElseThrow();
        String message = "get all students from class: " + course.getName();
        ApiResponse<Page<UserResponse>> response = new ApiResponse<>(1000,  message, teacherService.getAllUsersByCourse(id, no, size));
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/my-info/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses(){
        ApiResponse<List<CourseResponse>> response = new ApiResponse<>(1000, "get all my courses successfully", teacherService.getMyCourses());
        return ResponseEntity.ok().body(response);
    }
}
