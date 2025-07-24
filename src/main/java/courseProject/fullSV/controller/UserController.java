package courseProject.fullSV.controller;

import courseProject.fullSV.dto.request.*;
import courseProject.fullSV.dto.response.*;
import courseProject.fullSV.models.RefreshToken;
import courseProject.fullSV.repository.RefreshTokenRepo;
import courseProject.fullSV.service.UserService;
import courseProject.fullSV.service.authentication.JwtService;
import courseProject.fullSV.service.authentication.UserPrincipal;
import courseProject.fullSV.service.redis.BaseRedisService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@Slf4j
public class UserController {
    private final JwtService jwtService;
    private UserService userService;
    private AuthenticationManager manager;
    private RefreshTokenRepo refreshTokenRepo;
    private BaseRedisService baseRedisService;
    @Autowired
    public UserController(UserService userService, AuthenticationManager manager, JwtService jwtService, RefreshTokenRepo refreshTokenRepo, BaseRedisService baseRedisService) {
        this.userService = userService;
        this.manager = manager;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.baseRedisService = baseRedisService;
    }
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRequest request){
        ApiResponse<UserResponse> response = new ApiResponse<>(1000, "register successfully", userService.createUser(request));
        return ResponseEntity.ok().body(response);
    }
    @Transactional
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        log.warn("Username: " + principal.getUsername());
        log.warn("ROLE: " + principal.getAuthorities());
        String accessToken = jwtService.generateToken(principal);
        Claims accessClaims = jwtService.extractAllClaims(accessToken);
//        String key = "accessToken:" + accessClaims.getId();
//        baseRedisService.setValueTTL(key, accessToken, Duration.ofMinutes(15));
//        log.warn("access Token saved in redis");
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
    @PostMapping("/revoke-token")
    public ResponseEntity<ApiResponse> getRevokeToken(@RequestBody AccessTokenRequest request){
        jwtService.revokeToken(request.getToken());
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .message("revoked access token")
                .build());
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> getRefreshToken(@RequestBody RefreshTokenRequest request){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(1000, "refresh token successfully", jwtService.refreshToken(request));
        return ResponseEntity.ok().body(response);
    }
    @Transactional
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> getLogout(@RequestBody LogoutRequest request){
        ApiResponse<LogoutResponse> response = new ApiResponse<>(1000, "success", jwtService.getLogout(request));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("my-info")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(){
        return ResponseEntity.ok().body(ApiResponse.
                <UserResponse>builder()
                        .code(1000)
                        .message("get my info")
                        .data(userService.getMyInfo())
                .build());
    }
}
