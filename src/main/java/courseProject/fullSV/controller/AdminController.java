package courseProject.fullSV.controller;

import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.ApiResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.repository.RefreshTokenRepo;
import courseProject.fullSV.service.AdminService;
import courseProject.fullSV.service.UserService;
import courseProject.fullSV.service.authentication.JwtService;
import courseProject.fullSV.service.redis.BaseRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                                       @RequestParam(value = "pageSize", defaultValue = "5") int pageSize){
        Page<UserResponse> usersPage = adminService.getAllUsers(pageNo, pageSize);
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
}
