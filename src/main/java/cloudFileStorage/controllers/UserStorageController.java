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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class UserStorageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public UserStorageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(value = "path", required = false) String path,
                              @RequestParam("userObject") MultipartFile[] userObjects) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.uploadUserObjects(getUserStorageName(), path, userObjects);
        return getRedirectURL(path);
    }

    @PostMapping("/create/folder")
    public String createFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.createUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PostMapping("/download/file")
    public String downloadUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.downloadUserFile(getUserStorageName(), userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @PostMapping("/download/folder")
    public String downloadUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.downloadUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PatchMapping("/rename/file")
    public String renameUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO,
                                 @RequestParam("oldShortUserFileName") String oldShortUserFileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.renameUserFile(getUserStorageName(), oldShortUserFileName, userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @PatchMapping("/rename/folder")
    public String renameUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO,
                                 @RequestParam("oldShortUserFolderName") String oldShortUserFolderName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.renameUserFolder(getUserStorageName(), oldShortUserFolderName, userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @DeleteMapping(value = "/delete/file")
    public String deleteUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFile(getUserStorageName(), userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @DeleteMapping(value = "delete/folder")
    public String deleteUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @GetMapping("/search")
    public String searchUserObjects(@RequestParam("query") String searchQuery, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserObjectDTO> userObjectDTOList = userObjectsService.getUserObjectsBySearchQuery(getUserStorageName(), searchQuery);
        model.addAttribute("userObjectDTOList", userObjectDTOList);
        return "searchResult";
    }
}
