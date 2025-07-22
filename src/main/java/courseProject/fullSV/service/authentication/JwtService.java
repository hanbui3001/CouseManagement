package courseProject.fullSV.service.authentication;

import courseProject.fullSV.dto.request.AccessTokenRequest;
import courseProject.fullSV.dto.request.LogoutRequest;
import courseProject.fullSV.dto.request.RefreshTokenRequest;
import courseProject.fullSV.dto.response.AuthenticationResponse;
import courseProject.fullSV.dto.response.LogoutResponse;
import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.models.RefreshToken;
import courseProject.fullSV.repository.RefreshTokenRepo;
import courseProject.fullSV.service.redis.BaseRedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService {
    @Value("${jwt.sign-key}")
    String SIGN_KEY;
    @Value("${jwt.access-duration}")
    Long ACCESS_DURATION;
    @Value("${jwt.refresh-duration}")
    Long REFRESH_DURATION;
    @Autowired
    RefreshTokenRepo refreshTokenRepo;
    @Autowired
    UserAuthentication userAuthentication;
    @Autowired
    BaseRedisService baseRedisService;
    public String generateToken(UserPrincipal principal){
        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_DURATION))
                .id(UUID.randomUUID().toString())
                .claim("role", principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public SecretKey getSignKey(){
        byte[] bytes = Decoders.BASE64.decode(SIGN_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }
    public String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        if(!claims.isEmpty()) {
            Date expirationTime = claims.getExpiration();
            boolean expired = expirationTime.before(Date.from(Instant.now()));
            if (!expired) {
                return claims.getSubject();
            }
            else return null;
        }
        return null;
    }
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public String generateRefreshToken(UserPrincipal userPrincipal){
        String refreshToken = Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_DURATION) )
                .id(UUID.randomUUID().toString())
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();

        return refreshToken;
    }
    public boolean isRevoke(String token){
        Claims claims = extractAllClaims(token);
        String id = claims.getId();
        //baseRedisService.hashSet("accessToken: " + id, "accessToken", token);
        //log.warn("da luu access token vao redis");
        return baseRedisService.hasExisted("accessTokenRevoked", id);

    }
    public void revokeToken(String token){
        try {
            Claims claims = extractAllClaims(token);
            String id = claims.getId();
            long ttlMilis = claims.getExpiration().getTime() - System.currentTimeMillis();
            baseRedisService.hashSet("accessTokenRevoked", id, token);
            baseRedisService.setTimeToLive("accessTokenRevoked", Duration.ofMillis(ttlMilis));
            log.warn("da luu access token vao redis");
        } catch (Exception e) {
            log.error("cannot  revoke: " + e.getMessage());
        }
    }
    public AuthenticationResponse refreshToken(RefreshTokenRequest request){
        String id = request.getTokenId();
        RefreshToken refreshToken = refreshTokenRepo.findById(id).orElseThrow(() -> new WebException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        if(refreshToken.getExpiratedTime().before(new Date())){
            log.warn("refresh token is expired");
            refreshTokenRepo.deleteById(id);
            log.warn("refresh token is deleted");
            throw new WebException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        String oldToken = request.getOldToken();
        revokeToken(oldToken);
        UserDetails userDetails = userAuthentication.loadUserByUsername(refreshToken.getUsername());
        UserPrincipal principal = (UserPrincipal) userDetails;
        String newAccessToken = generateToken(principal);
        return AuthenticationResponse.builder()
                .acessToken(newAccessToken)
                .isAuthenticated(true)
                .build();
    }
    public LogoutResponse getLogout(LogoutRequest request){
        Claims claims = extractAllClaims(request.getToken());
        String id = claims.getId();
        String username = claims.getSubject();
        if(!baseRedisService.hasExisted("accessTokenRevoked", id)) {
            revokeToken(request.getToken());
        }
        else log.warn("error revoke");
        refreshTokenRepo.deleteByUsername(username);
        log.warn("refresh-token revoked");
        log.warn("logout");
        return LogoutResponse.builder()
                .token(request.getToken())
                .message("logout success")
                .build();
    }
    @PostConstruct
    public void init(){
        //System.out.println(Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()));
        log.warn("Sign key: " + SIGN_KEY);
    }
}
