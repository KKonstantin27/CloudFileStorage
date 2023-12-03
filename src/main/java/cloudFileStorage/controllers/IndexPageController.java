package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.UserDetails;
import cloudFileStorage.services.UserObjectsService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class IndexPageController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public IndexPageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @GetMapping("/")
    public String getIndexPage(@RequestParam(value = "path", required = false) String path, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        int userId = ((UserDetails) authentication.getPrincipal()).getUser().getId();
        String userStorageName = "user-" + userId + "-files/";
        List<UserObjectDTO> userObjectDTOList;

        if (path == null) {
            userObjectDTOList = userObjectsService.getObjects(userStorageName);
//            for (Result<Item> object : objects) {
//                System.out.println(object.get().objectName());
//            }
        } else {
            userObjectDTOList = userObjectsService.getObjects(userStorageName + path + "/");
//            for (Result<Item> object : objects) {
//                System.out.println(object.get().objectName());
//            }
        }
        model.addAttribute("userObjectDTOList", userObjectDTOList);
        return "index";
    }
}
