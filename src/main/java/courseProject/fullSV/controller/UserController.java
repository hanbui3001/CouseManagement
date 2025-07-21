package courseProject.fullSV.controller;

import courseProject.fullSV.dto.request.LoginRequest;
import courseProject.fullSV.dto.request.RefreshTokenRequest;
import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.ApiResponse;
import courseProject.fullSV.dto.response.AuthenticationResponse;
import courseProject.fullSV.dto.response.LoginResponse;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.models.RefreshToken;
import courseProject.fullSV.repository.RefreshTokenRepo;
import courseProject.fullSV.service.UserService;
import courseProject.fullSV.service.authentication.JwtService;
import courseProject.fullSV.service.authentication.UserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {
    private final JwtService jwtService;
    private UserService userService;
    private AuthenticationManager manager;
    private RefreshTokenRepo refreshTokenRepo;
    @Autowired
    public UserController(UserService userService, AuthenticationManager manager, JwtService jwtService, RefreshTokenRepo refreshTokenRepo) {
        this.userService = userService;
        this.manager = manager;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRequest request){
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, "register successfully", userService.createUser(request));
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        log.warn("Username: " + principal.getUsername());
        log.warn("ROLE: " + principal.getAuthorities());
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);
        Claims refreshClaim = jwtService.extractAllClaims(refreshToken);
        String refreshId = refreshClaim.getId();
        refreshTokenRepo.save(RefreshToken.builder()
                .id(refreshId)
                .username(principal.getUsername())
                .token(refreshToken)
                .expiratedTime(refreshClaim.getExpiration())
                .build());
        log.warn("refresh_token saved");
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .code(1000)
                .message("Login successfully")
                .data(LoginResponse.builder()
                        .username(principal.getUsername())
                        .accessToken(accessToken)
                        .refreshToken(refreshId)
                        .build())
                .build();
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> getRefreshToken(@RequestBody RefreshTokenRequest request){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(1000, "refresh token successfully", jwtService.refreshToken(request));
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                                       @RequestParam(value = "pageSize", defaultValue = "5") int pageSize){
        Page<UserResponse> usersPage = userService.getAllUsers(pageNo, pageSize);
        ApiResponse<Page<UserResponse>> response = new ApiResponse<>(1000, "Page users success", usersPage);
        return ResponseEntity.ok().body(response);
    }
}
