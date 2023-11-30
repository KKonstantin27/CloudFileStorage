package cloudFileStorage.controllers;

import cloudFileStorage.security.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexPageController {

    @GetMapping("/")
    public String getIndexPage(@RequestParam(value = "path", required = false) String path, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            model.addAttribute("id", ((UserDetails) authentication.getDetails()).getUser().getId());

        }
        return "index";
    }
}
