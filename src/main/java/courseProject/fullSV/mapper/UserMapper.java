package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.models.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toUsers(UserRequest userRequest);
    UserResponse toUserResponse(Users users);
}
