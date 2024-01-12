package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserDTO;
import cloudFileStorage.exceptions.StorageException;
import cloudFileStorage.models.User;
import cloudFileStorage.services.UserDetailsService;
import cloudFileStorage.services.UserObjectsService;
import cloudFileStorage.utils.UsersMapper;
import cloudFileStorage.utils.UsersValidator;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {
    private final UserDetailsService userDetailsService;
    private final UserObjectsService userObjectsService;

    private final UsersValidator usersValidator;
    private final UsersMapper usersMapper;

    @Autowired
    public AuthController(UserDetailsService userDetailsService, UserObjectsService userObjectsService, UsersValidator usersValidator, UsersMapper usersMapper) {
        this.userDetailsService = userDetailsService;
        this.userObjectsService = userObjectsService;
        this.usersValidator = usersValidator;
        this.usersMapper = usersMapper;
    }

    @GetMapping("/signIn")
    public String getAuthPage(@RequestParam(value = "error", required = false) String error, HttpServletResponse response) {
//        if (error != null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return "auth/signIn";
//        }
        return "auth/signIn";
    }

    @GetMapping("/signUp")
    public String getRegistrationPage(@ModelAttribute("userDTO") UserDTO userDTO) {
        return "auth/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, HttpServletResponse response) throws StorageException {
        usersValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "auth/signUp";
        }
        User savedUser = userDetailsService.signUp(usersMapper.convertToUser(userDTO));
        userObjectsService.createUserStorage("user-" + savedUser.getId() + "-files");
        return "redirect:/auth/success";
    }

    @GetMapping("/success")
    public String getSuccessPage(@ModelAttribute("userDTO") UserDTO userDTO) {
        return "auth/success";
    }
}
