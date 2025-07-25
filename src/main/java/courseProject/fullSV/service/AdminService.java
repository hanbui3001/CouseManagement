package courseProject.fullSV.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.enums.Roles;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.mapper.UserMapper;
import courseProject.fullSV.models.Role;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.RoleRepo;
import courseProject.fullSV.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminService {
    UserMapper userMapper;
    PasswordEncoder encoder;
    UserRepo userRepo;
    RoleRepo roleRepo;

    @Autowired
    public AdminService(UserMapper userMapper, PasswordEncoder encoder, UserRepo userRepo, RoleRepo roleRepo) {
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(int pageNo, int pageSize){
        System.out.println("Khong hoat dong pre authorize");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Users> usersPage = userRepo.findAll(pageable);
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
}
