package courseProject.fullSV.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String username;
    String accessToken;
    //String refreshToken;
}
