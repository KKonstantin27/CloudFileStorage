package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BaseController {
    public String getUserStorageName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = ((UserDetails) authentication.getPrincipal()).getUser().getId();
        return  "user-" + userId + "-files/";
    }

    public String getRedirectURL(UserObjectDTO userObjectDTO) throws UnsupportedEncodingException {
        return userObjectDTO.getPath().isEmpty() ? "redirect:/" : "redirect:/?path=" + URLEncoder.encode(userObjectDTO.getPath(), "UTF-8");
    }

    public String getRedirectURL(String path) throws UnsupportedEncodingException {
        return path.isEmpty() ? "redirect:/" : "redirect:/?path=" + URLEncoder.encode(path, "UTF-8");
    }
    public void setErrorsRedirectAttribute(BindingResult bindingResult, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        redirectAttributes.addFlashAttribute("errors", errors);
    }
}
