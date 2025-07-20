package courseProject.fullSV.controller;

import courseProject.fullSV.dto.request.LoginRequest;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.ApiResponse;
import courseProject.fullSV.dto.response.LoginResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.service.UserService;
import courseProject.fullSV.service.authentication.JwtService;
import courseProject.fullSV.service.authentication.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@Slf4j
public class UserController {
    private final JwtService jwtService;
    private UserService userService;
    private AuthenticationManager manager;
    @Autowired
    public UserController(UserService userService, AuthenticationManager manager, JwtService jwtService) {
        this.userService = userService;
        this.manager = manager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserRequest request){
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, "register successfully", userService.createUser(request));
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        log.warn("Username: " + principal.getUsername());
        log.warn("ROLE: " + principal.getAuthorities());
        String token = jwtService.generateToken(principal);
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .code(1000)
                .message("Login successfully")
                .data(LoginResponse.builder()
                        .username(principal.getUsername())
                        .accessToken(token)
                        .build())
                .build();
        return ResponseEntity.ok().body(response);
    }
}
