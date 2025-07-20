package courseProject.fullSV.dto.response;

import courseProject.fullSV.enums.Roles;
import courseProject.fullSV.models.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String firstname;
    String lastname;
    String email;
    String phone;
    String username;
    String password;
    Set<Role> role;

}
