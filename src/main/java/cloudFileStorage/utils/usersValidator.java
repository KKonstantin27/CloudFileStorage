package cloudFileStorage.utils;

import cloudFileStorage.models.User;
import cloudFileStorage.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class usersValidator implements Validator {
    private final UserDetailsService userDetailsService;

    @Autowired
    public usersValidator(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        Optional<User> userOptional = userDetailsService.loadUserOptionalByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }
    }
}
