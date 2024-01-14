package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.exceptions.StorageException;
import cloudFileStorage.services.UserObjectsService;
import cloudFileStorage.utils.BreadcrumbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class IndexPageController extends BaseController {

    private final UserObjectsService userObjectsService;
    private final BreadcrumbsUtil breadcrumbsUtil;

    @Autowired
    public IndexPageController(UserObjectsService userObjectsService, BreadcrumbsUtil breadcrumbsUtil) {
        this.userObjectsService = userObjectsService;
        this.breadcrumbsUtil = breadcrumbsUtil;
    }

    @GetMapping("/")
    public String getIndexPage(@RequestParam(value = "path", required = false) String path,
                               @ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO,
                               @ModelAttribute("userFileDTO") UserFileDTO userFileDTO,
                               Model model) throws StorageException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        List<UserObjectDTO> userObjectDTOList = userObjectsService.getUserObjects(getUserStorageName(), path);
        Map<String, String> breadcrumbs = breadcrumbsUtil.buildBreadcrumbs(getUserStorageName(), path);

        model.addAttribute("userStorageName", getUserStorageName());
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("userObjectDTOList", userObjectDTOList);

        return "index";
    }
}
