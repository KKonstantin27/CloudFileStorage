package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.security.UserDetails;
import cloudFileStorage.services.UserObjectsService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserStorageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public UserStorageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(value = "path", required = false) String path,
                              @RequestParam("userObject") MultipartFile userObject) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.uploadUserObject(getUserStorageName() + path, userObject);
        if (path.isEmpty()) {
            return "redirect:/";
        } else {
            return "redirect:/?path=" + path;
        }
    }

    @PostMapping("/create-folder")
    public String createFolder(@RequestParam(value = "path", required = false) String path,
                               @RequestParam("userObjectName") String userObjectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.createFolder(getUserStorageName() + path + userObjectName + "/");
        if (path.isEmpty()) {
            return "redirect:/";
        } else {
            return "redirect:/?path=" + path;
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public String deleteUserObject(@RequestParam(value = "path", required = false) String path,
                                   @RequestParam("userObjectName") String userObjectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserObject(getUserStorageName() + path + userObjectName);
        if (path.isEmpty()) {
            return "redirect:/";
        } else {
            return "redirect:/?path=" + path;
        }
    }
}
