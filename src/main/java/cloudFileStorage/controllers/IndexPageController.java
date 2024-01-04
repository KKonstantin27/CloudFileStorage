package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.UserDetails;
import cloudFileStorage.services.UserObjectsService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.*;

@Controller
public class IndexPageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public IndexPageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @GetMapping("/")
    public String getIndexPage(@RequestParam(value = "path", required = false) String path,
                               @ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO,
                               @ModelAttribute("userFileDTO") UserFileDTO userFileDTO,
                               Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        List<UserObjectDTO> userObjectDTOList = userObjectsService.getUserObjects(getUserStorageName(), path);
        Map<String, String> breadcrumbs = userObjectsService.buildBreadcrumbs(getUserStorageName(), path);
        model.addAttribute("userStorageName", getUserStorageName());
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("userObjectDTOList", userObjectDTOList);
        return "index";
    }
}
