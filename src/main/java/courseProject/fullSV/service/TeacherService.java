package courseProject.fullSV.service;

import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.Course;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.CourseRepo;
import courseProject.fullSV.repository.EnrollmentRepo;
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
import org.springframework.stereotype.Service;

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
    @PreAuthorize("hasRole('TEACHER')")
    public Page<UserResponse> getAllUsersByCourse(String id, int pageNo, int pageSize){
        if(!courseRepo.existsById(id)) throw new WebException(ErrorCode.COURSE_NOT_FOUND);
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, Sort.by("id").ascending());
        Page<Users> responses = enrollmentRepo.findAllStudentsByCourse(id, pageable);
        return responses.map(users -> userMapper.toUserResponse(users));
    }
}
