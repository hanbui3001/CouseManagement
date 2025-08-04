package courseProject.fullSV.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import courseProject.fullSV.dto.request.CourseRequest;
import courseProject.fullSV.dto.request.SubjectRequest;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.CourseResponse;
import courseProject.fullSV.dto.response.SubjectResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.enums.Roles;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.CourseMapper;
import courseProject.fullSV.mapper.SubjectMapper;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Role;
import courseProject.fullSV.models.Subject;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.CourseRepo;
import courseProject.fullSV.repository.RoleRepo;
import courseProject.fullSV.repository.SubjectRepo;
import courseProject.fullSV.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AdminService {
    UserMapper userMapper;
    UserRepo userRepo;
    RoleRepo roleRepo;
    CourseMapper courseMapper;
    CourseRepo courseRepo;
    SubjectRepo subjectRepo;
    SubjectMapper subjectMapper;
    @Autowired
    public AdminService(UserMapper userMapper, UserRepo userRepo, RoleRepo roleRepo, CourseMapper courseMapper, CourseRepo courseRepo, SubjectRepo subjectRepo, SubjectMapper subjectMapper) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.courseMapper = courseMapper;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
        this.subjectMapper = subjectMapper;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(String role,int pageNo, int pageSize){
        System.out.println("Khong hoat dong pre authorize");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").ascending());
        Page<Users> usersPage = userRepo.findAllByRole(role, pageable);
        return usersPage.map(users -> userMapper.toUserResponse(users));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserById(String id, UserRequest request){
        Users users = userRepo.findById(id).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUserFromRequest(request, users);
        users.setRole(convertToRole(request.getRole()));
        userRepo.save(users);
        return userMapper.toUserResponse(users);
    }
    private Set<Role> convertToRole(Set<String> roles){
        return roles.stream()
                .map(role -> roleRepo.findByName(role).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND)))
                .collect(Collectors.toSet());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(String id){
        Users users = userRepo.findById(id).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        userRepo.delete(users);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse createSubject(SubjectRequest request){
        Subject subject = subjectMapper.toSubject(request);
        log.warn("Da save subject");
        return subjectMapper.toSubjectResponse(subjectRepo.save(subject));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponse createCourse(CourseRequest request){
        Subject subject = subjectRepo.findByName(request.getSubject())
                .orElseGet(() -> subjectRepo.save(Subject.builder()
                                .name(request.getSubject())
                        .build()));

        Course course = courseMapper.toCourse(request);
        subject.addCourse(course);
        return courseMapper.toCourseResponse(courseRepo.save(course));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserAsRole(String id, String role){
        Users users = userRepo.findByIdWithCourse(id).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        Role userRole = roleRepo.findByName(role).orElseThrow(() -> new WebException(ErrorCode.ROLE_NOT_FOUND));
        users.setRole(new HashSet<>(Set.of(userRole)));
        return userMapper.toUserResponse(userRepo.save(users));
    }
}
