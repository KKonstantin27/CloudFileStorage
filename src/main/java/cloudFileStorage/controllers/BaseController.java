package cloudFileStorage.controllers;

import cloudFileStorage.security.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    public String getUserStorageName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = ((UserDetails) authentication.getPrincipal()).getUser().getId();
        return  "user-" + userId + "-files/";
    }
}
