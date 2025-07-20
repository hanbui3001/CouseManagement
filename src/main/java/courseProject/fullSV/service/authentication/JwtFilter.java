package courseProject.fullSV.service.authentication;

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
        String auHeader = request.getHeader("Authorization");
        String token;
        String username = null;
        if(auHeader != null && auHeader.startsWith("Bearer ")){
            token = auHeader.substring(7);
            log.warn(token);
            username = jwtService.extractUsername(token);
            log.warn("DEBUG: " + username);

        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails= userAuthentication.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken userPassToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(userPassToken);
        }
        filterChain.doFilter(request, response);
    }
}
