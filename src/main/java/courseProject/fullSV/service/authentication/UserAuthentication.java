package courseProject.fullSV.service.authentication;

import courseProject.fullSV.enums.ErrorCode;
import courseProject.fullSV.exception.WebException;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthentication implements UserDetailsService {
    private UserRepo userRepo;
    @Autowired
    public UserAuthentication(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepo.findByUsername(username).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        return new UserPrincipal(users);
    }
}
