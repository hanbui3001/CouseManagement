package courseProject.fullSV.service.authentication;

import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.exception.WebException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Service
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserAuthentication userAuthentication;
    @Autowired
    public JwtFilter(JwtService jwtService, UserAuthentication userAuthentication) {
        this.jwtService = jwtService;
        this.userAuthentication = userAuthentication;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/logout")) {
            log.info("Bypassing JwtFilter for /logout");
            filterChain.doFilter(request, response);
            return;
        }
        String auHeader = request.getHeader("Authorization");
        String token;
        String username = null;
        try {

            if (auHeader != null && auHeader.startsWith("Bearer ")) {
                token = auHeader.substring(7);
                log.warn(token);
                if (jwtService.isRevoke(token)) {
                   response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    log.warn("access token is revoked");
                    //throw new WebException(ErrorCode.UNAUTHENTICATED);
                    //filterChain.doFilter(request, response);
                    return;
                }
                username = jwtService.extractUsername(token);
                log.warn("DEBUG: " + username);
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userAuthentication.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken userPassToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(userPassToken);
            }
        }catch (SignatureException e){
            log.error("wrong signature jwt: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch (IllegalArgumentException e){
            log.error("sai dinh dang jwt: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch (ExpiredJwtException e){
            log.error("expired jwt" + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
