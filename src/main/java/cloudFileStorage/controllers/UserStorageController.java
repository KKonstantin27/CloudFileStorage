package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserStorageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public UserStorageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

//    @PostMapping("/upload")
//    public String uploadFiles(@RequestParam(value = "path", required = false) String path,
//                              @RequestParam("userObject") MultipartFile userObject) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        userObjectsService.uploadUserObject(getUserStorageName() + path, userObject);
//        if (path.isEmpty()) {
//            return "redirect:/";
//        } else {
//            return "redirect:/?path=" + path;
//        }
//    }

    @PostMapping("/create-folder")
    public String createFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.createFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PatchMapping("/rename-file")
    public String renameUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO,
                                 @RequestParam("oldName") String oldUserFileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.renameUserFile(getUserStorageName(), oldUserFileName, userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @DeleteMapping(value = "/delete-file")
    public String deleteUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFile(getUserStorageName(), userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @DeleteMapping(value = "delete-folder")
    public String deleteUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }
}
