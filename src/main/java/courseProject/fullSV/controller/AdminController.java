package courseProject.fullSV.controller;

import courseProject.fullSV.dto.request.CourseRequest;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.ApiResponse;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.TeacherResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.repository.RefreshTokenRepo;
import courseProject.fullSV.service.AdminService;
import courseProject.fullSV.service.UserService;
import courseProject.fullSV.service.authentication.JwtService;
import courseProject.fullSV.service.redis.BaseRedisService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private  JwtService jwtService;
    private AdminService adminService;
    private AuthenticationManager manager;
    private RefreshTokenRepo refreshTokenRepo;
    private BaseRedisService baseRedisService;
    @Autowired
    public AdminController(AdminService adminService, AuthenticationManager manager, JwtService jwtService, RefreshTokenRepo refreshTokenRepo, BaseRedisService baseRedisService) {
        this.adminService = adminService;
        this.manager = manager;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.baseRedisService = baseRedisService;
    }
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@RequestParam(value = "role",required = false) String role,
                                                                        @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                                       @RequestParam(value = "pageSize", defaultValue = "5") int pageSize){
        Page<UserResponse> usersPage = adminService.getAllUsers(role, pageNo, pageSize);
        ApiResponse<Page<UserResponse>> response = new ApiResponse<>(1000, "Page users success", usersPage);
        return ResponseEntity.ok().body(response);
    }
    @Transactional
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserById(@RequestParam(value = "id") String id,
                                                                    @RequestBody UserRequest request){
        String message = "update user with id: {" + id + "} successfully";
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, message, adminService.updateUserById(id, request));
        return ResponseEntity.ok().body(response);
    }
    @Transactional
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse> deleteUserById(@RequestParam(value = "id") String id){
        adminService.deleteUserById(id);
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .code(1000)
                        .message("delete user successfully")
                .build());
    }
    @Transactional
    @PostMapping("/course")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@RequestBody CourseRequest request){
        ApiResponse<CourseResponse> response = new ApiResponse<>(1000, "create course successfully", adminService.createCourse(request));
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/role")
    public ResponseEntity<ApiResponse<UserResponse>> setUserAsRole(@RequestParam(value = "user_id") String id,
                                                                   @RequestParam(value = "role") String role){
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, "set role successfully", adminService.getUserAsRole(id, role));
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/teacher/course")
    public ResponseEntity<ApiResponse<TeacherResponse>> addTeacherToCourse(@RequestParam(value = "teacher_id") String teacherId,
                                                                           @RequestParam(value = "course_id") String courseId){
        ApiResponse<TeacherResponse> response = new ApiResponse<>(1000, "add teacher to course successfully", adminService.addTeacherToCourse(teacherId, courseId));
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/teacher/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getTeacherByCourse(@PathVariable String id){
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, "get teacher successfully", adminService.getTeacherByCourseId(id));
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/courses/student/{id}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesById(@PathVariable String id){
        ApiResponse<List<CourseResponse>> response = new ApiResponse<>(1000, "Get list courses successfully", adminService.getAllCourseByStudentId(id));
        return ResponseEntity.ok().body(response);
    }
}
