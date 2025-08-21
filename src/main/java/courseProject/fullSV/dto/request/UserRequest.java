package courseProject.fullSV.dto.request;

import courseProject.fullSV.models.Role;
import courseProject.fullSV.validator.Email;
import courseProject.fullSV.validator.PhoneConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    String firstname;
    @NotBlank
    String lastname;
    @Email(message = "INVALID_EMAIL")
    @NotBlank
    String email;
    @PhoneConstraint(message = "INVALID_PHONE_NUMBER")
    @NotBlank
    String phone;
    @Size(min = 3, message = "USERNAME_NOT_VALID")
    @NotBlank
    String username;
    @Size(min = 4, message = "PASSWORD_NOT_VALID")
    @NotBlank
    String password;
    Set<String> role;
}
