package courseProject.fullSV.service;

import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.EnrollmentResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.enums.Roles;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.CourseMapper;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Enrollment;
import courseProject.fullSV.models.Role;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.CourseRepo;
import courseProject.fullSV.repository.EnrollmentRepo;
import courseProject.fullSV.repository.RoleRepo;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    UserMapper userMapper;
    PasswordEncoder encoder;
    UserRepo userRepo;
    RoleRepo roleRepo;
    CourseRepo courseRepo;
    EnrollmentRepo enrollmentRepo;
    CourseMapper courseMapper;
    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder encoder, UserRepo userRepo, RoleRepo roleRepo, CourseRepo courseRepo, EnrollmentRepo enrollmentRepo,
                       CourseMapper courseMapper) {
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.courseMapper = courseMapper;
    }
    public UserResponse createUser(UserRequest request){
        request.setPassword(encoder.encode(request.getPassword()));
        Users users = userMapper.toUsers(request);
        if(userRepo.existsByUsername(users.getUsername())){
            throw new WebException(ErrorCode.USER_EXISTED);
        }
        Users savedUser = userRepo.save(users);
        Role role = roleRepo.findByName(Roles.ROLE_USER.name())
                        .orElseGet(() -> {
                            Role userRole = Role.builder()
                                    .name(Roles.ROLE_USER.name())
                                    .description("role user")
                                    .build();
                            return roleRepo.save(userRole);
                        });
        savedUser.setRole(new HashSet<>(Set.of(role)));
        Users finalUser = userRepo.save(savedUser);
        log.warn("LOG: user registered");
        return userMapper.toUserResponse(finalUser);

    }
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext().getAuthentication();
        String username = context.getName();
        Users user = userRepo.findByUsername(username).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasAnyRole('USER', 'STUDENT')")
    public EnrollmentResponse registerCourse(String courseId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String userId = principal.getId();
        Users users = userRepo.findByIdWithCourse(userId).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new WebException(ErrorCode.COURSE_NOT_FOUND));
        if(enrollmentRepo.existsByUsersAndCourse(users, course)){
            throw new WebException(ErrorCode.ALREADY_ENROLLED);
        }
        Enrollment enrollment = Enrollment.builder()
                .timeEnroll(LocalDateTime.now())
                .active("ACTIVE")
                .users(users)
                .course(course)
                .build();
        log.warn("dang ki hoc vien lop: ", users.getCourseList());
        users.setRole(convertToRole(Set.of(Roles.ROLE_STUDENT.name())));
        enrollmentRepo.save(enrollment);
        userRepo.save(users);
        log.warn("user become student");
        return EnrollmentResponse.builder()
                .timeEnroll(enrollment.getTimeEnroll())
                .active(enrollment.getActive())
                .build();
    }
    private Set<Role> convertToRole(Set<String> roles){
        return roles.stream()
                .map(role -> roleRepo.findByName(role).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND)))
                .collect(Collectors.toSet());
    }
    public List<CourseResponse> getAllCourses(){
        List<Course> courses = courseRepo.findAll();
        log.warn("lay ra full course");
        return courses.stream().map(course -> courseMapper.toCourseResponse(course)).toList();
    }
    public Page<UserResponse> getAllTeachers(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").ascending());
        Page<Users> usersPage = userRepo.findAllTeachers(Roles.ROLE_TEACHER.name(), pageable);
        return usersPage.map(users -> userMapper.toUserResponse(users));
    }

}
