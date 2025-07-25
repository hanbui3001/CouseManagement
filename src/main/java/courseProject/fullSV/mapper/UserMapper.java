package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.models.Users;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role",  ignore = true)
    Users toUsers(UserRequest userRequest);
    UserResponse toUserResponse(Users users);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "role",  ignore = true)
     void updateUserFromRequest(UserRequest request,@MappingTarget Users users);
}
