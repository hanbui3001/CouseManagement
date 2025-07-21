package courseProject.fullSV.config;

import courseProject.fullSV.models.Role;
import courseProject.fullSV.models.Users;
import courseProject.fullSV.repository.RoleRepo;
import courseProject.fullSV.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class adminConfig {
    @Autowired
    private PasswordEncoder encoder;
    @Bean
    public ApplicationRunner applicationRunner(UserRepo userRepo, RoleRepo roleRepo){
        return args -> {
            Role adminrole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(
                            Role.builder()
                                    .name("ROLE_ADMIN")
                                    .description("admin role")
                                    .build()
                    ));
            if(!userRepo.existsByUsername("admin")){
                Users users = Users.builder()
                        .firstname("han")
                        .lastname("bui")
                        .username("admin")
                        .password(encoder.encode("admin"))
                        .role(Set.of(adminrole))
                        .build();
                userRepo.save(users);
            }
        };
    }
}
