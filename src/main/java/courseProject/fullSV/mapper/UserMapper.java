package courseProject.fullSV.mapper;

import courseProject.fullSV.dto.request.UserRequest;
import courseProject.fullSV.dto.response.UserResponse;
import courseProject.fullSV.models.Users;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toUsers(UserRequest userRequest);
    UserResponse toUserResponse(Users users);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     void updateUserFromRequest(UserRequest request,@MappingTarget Users users);
}
