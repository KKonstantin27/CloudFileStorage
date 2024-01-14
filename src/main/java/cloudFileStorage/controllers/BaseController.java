package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.CustomUserDetails;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class BaseController {
    public String getUserStorageName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        return "user-" + userId + "-files/";
    }

    public String getRedirectURL(UserObjectDTO userObjectDTO) {
        return userObjectDTO.getPath().isEmpty() ? "redirect:/" : "redirect:/?path=" +
                URLEncoder.encode(userObjectDTO.getPath(), StandardCharsets.UTF_8);
    }

    public String getRedirectURL(String path) {
        return path.isEmpty() ? "redirect:/" : "redirect:/?path="
                + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public void setErrorsRedirectAttribute(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        List<String> errors = bindingResult
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        redirectAttributes.addFlashAttribute("errors", errors);
    }
}
