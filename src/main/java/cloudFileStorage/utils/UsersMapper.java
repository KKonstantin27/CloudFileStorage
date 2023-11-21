package cloudFileStorage.utils;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public UsersMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
