package courseProject.fullSV.service.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.sign-key}")
    String SIGN_KEY;
    @Value("${jwt.access-duration}")
    Long ACCESS_DURATION;
    public String generateToken(UserPrincipal principal){
        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_DURATION))
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
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    @PostConstruct
    public void init(){
        //System.out.println(Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()));
        log.warn("Sign key: " + SIGN_KEY);
    }
}
