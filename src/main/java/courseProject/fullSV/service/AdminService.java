package courseProject.fullSV.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import courseProject.fullSV.dto.request.CourseRequest;
import courseProject.fullSV.dto.request.ScheduleRequest;
import courseProject.fullSV.dto.request.SubjectRequest;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.*;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.enums.Roles;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.CourseMapper;
import courseProject.fullSV.mapper.ScheduleMapper;
import courseProject.fullSV.mapper.SubjectMapper;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.*;
import courseProject.fullSV.repository.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.EscapedErrors;

import java.util.HashSet;
import java.util.List;
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
    EnrollmentRepo enrollmentRepo;
    ScheduleMapper scheduleMapper;
    ScheduleRepo scheduleRepo;
    @Autowired
    public AdminService(UserMapper userMapper, UserRepo userRepo, RoleRepo roleRepo, CourseMapper courseMapper, CourseRepo courseRepo, SubjectRepo subjectRepo, SubjectMapper subjectMapper,
                        EnrollmentRepo enrollmentRepo, ScheduleMapper scheduleMapper, ScheduleRepo scheduleRepo) {
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.courseMapper = courseMapper;
        this.courseRepo = courseRepo;
        this.subjectRepo = subjectRepo;
        this.subjectMapper = subjectMapper;
        this.enrollmentRepo = enrollmentRepo;
        this.scheduleMapper = scheduleMapper;
        this.scheduleRepo = scheduleRepo;
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
    @PreAuthorize("hasRole('ADMIN')")
    public TeacherResponse addTeacherToCourse(String teacherId, String courseId){
        Course course = courseRepo.findByIdWithNull(courseId).orElseThrow(() -> new WebException(ErrorCode.COURSE_NOT_FOUND));
        Users teacher = userRepo.findByIdWithCourse(teacherId).orElseThrow(() -> new WebException(ErrorCode.TEACHER_NOT_FOUND));
        course.setTeacher(teacher);
        courseRepo.save(course);
        log.warn("da them teacher " + teacher.getLastname() +  " vao course " + course.getName());
        return TeacherResponse.builder()
                .id(teacherId)
                .name(teacher.getFirstname() + " " + teacher.getLastname())
                .subject(course.getSubject().getName())
                .courseName(course.getName())
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getTeacherByCourseId(String id){
        Users teacher = courseRepo.findTeacherByCourseId(id).orElseThrow(() -> new WebException(ErrorCode.COURSE_NOT_FOUND));
        return userMapper.toUserResponse(teacher);
    }
    //get course by student id
    @PreAuthorize("hasRole('ADMIN')")
    //@Transactional
    public List<CourseResponse> getAllCourseByStudentId(String id){
        List<Course> courses = enrollmentRepo.findCourseByStudentId(id);
        if(courses.isEmpty()) throw new WebException(ErrorCode.COURSE_NOT_FOUND);
        else return courses.stream().map(course -> courseMapper.toCourseResponse(course)).toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(String id){
        Users users = userRepo.findById(id).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(users);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public ScheduleResponse createScheduleById(String courseId, ScheduleRequest scheduleRequest){
        Course course = courseRepo.findByCourseIdWithScheduleNull(courseId).orElseThrow(() -> new WebException(ErrorCode.COURSE_NOT_FOUND));
        CourseSchedule courseSchedule = scheduleMapper.toSchedule(scheduleRequest);
        courseSchedule.setCourse(course);
        course.setCourseSchedule(courseSchedule);
        log.warn("them lich vao course !!!");
        courseRepo.save(course);
        return ScheduleResponse.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .days(courseSchedule.getDays())
                .timeStart(courseSchedule.getTimeStart())
                .timeEnd(courseSchedule.getTimeEnd())
                .dayStart(courseSchedule.getDayStart())
                .dayEnd(courseSchedule.getDayEnd())
                .build();
    }
}
