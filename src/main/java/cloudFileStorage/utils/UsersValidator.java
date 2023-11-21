package cloudFileStorage.utils;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.models.User;
import cloudFileStorage.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UsersValidator implements Validator {
    private final UserDetailsService userDetailsService;

    @Autowired
    public UsersValidator(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        Optional<User> userOptional = userDetailsService.loadUserOptionalByUsername(userDTO.getUsername());
        if (userOptional.isEmpty()) {
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }
        if (!userDTO.getPassword().equals(userDTO.getRepeatPassword())) {
            errors.rejectValue("password", "", "Ввёденные пароли не совпадают");
        }
    }
}
