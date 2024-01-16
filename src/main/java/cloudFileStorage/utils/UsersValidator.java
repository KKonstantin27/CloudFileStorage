package cloudFileStorage.utils;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.models.User;
import cloudFileStorage.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UsersValidator implements Validator {
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UsersValidator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        Optional<User> userOptional = customUserDetailsService.loadUserOptionalByUsername(userDTO.getUsername());
        if (userOptional.isPresent()) {
            errors.rejectValue("username", "", "Username already in use");
        }
        if (!userDTO.getPassword().equals(userDTO.getRepeatPassword())) {
            errors.rejectValue("repeatPassword", "", "Passwords don't match");
        }
    }
}
