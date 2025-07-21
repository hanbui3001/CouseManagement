package courseProject.fullSV.dto.request;

import courseProject.fullSV.models.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String firstname;
    String lastname;
    String email;
    String phone;
    @Size(min = 3, message = "USERNAME_NOT_VALID")
    String username;
    @Size(min = 3, message = "PASSWORD_NOT_VALID")
    String password;
    Set<Role> role;
}
