package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
}
