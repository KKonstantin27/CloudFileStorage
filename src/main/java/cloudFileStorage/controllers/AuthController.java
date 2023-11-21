package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.models.User;
import cloudFileStorage.services.UserDetailsService;
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

    @Autowired
    public AuthController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/login")
    public String getAuthPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid UserDTO userDTO, BindingResult bindingResult) {
//        userDetailsService.signUp();
        return "auth/register";
    }
}
