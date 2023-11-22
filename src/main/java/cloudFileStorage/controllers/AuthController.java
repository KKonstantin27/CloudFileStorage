package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.services.UserDetailsService;
import cloudFileStorage.utils.UsersMapper;
import cloudFileStorage.utils.UsersValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final UsersValidator usersValidator;
    private final UsersMapper usersMapper;

    @Autowired
    public AuthController(UserDetailsService userDetailsService, UsersValidator usersValidator, UsersMapper usersMapper) {
        this.userDetailsService = userDetailsService;
        this.usersValidator = usersValidator;
        this.usersMapper = usersMapper;
    }

    @GetMapping("/signIn")
    public String getAuthPage() {
        return "auth/signIn";
    }

    @GetMapping("/signUp")
    public String getRegistrationPage() {
        return "auth/signUp";
    }

    @PostMapping("/signUp")
    public String register(@ModelAttribute @Valid UserDTO userDTO, BindingResult bindingResult) {
        usersValidator.validate(userDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/signUp";
        }

        userDetailsService.signUp(usersMapper.convertToUser(userDTO));
        return "redirect:auth/success";
    }
}
