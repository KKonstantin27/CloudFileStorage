package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.services.UserObjectsService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class UserStorageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public UserStorageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @GetMapping("/search")
    public String searchUserObjects(@RequestParam("query") String searchQuery, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserObjectDTO> userObjectDTOList = userObjectsService.getUserObjectsBySearchQuery(getUserStorageName(), searchQuery);
        model.addAttribute("userObjectDTOList", userObjectDTOList);
        return "searchResult";
    }

    @PostMapping("/create/folder")
    public String createFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.createUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(value = "path", required = false) String path,
                              @RequestParam("userObject") MultipartFile[] userObjects) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.uploadUserObjects(getUserStorageName(), path, userObjects);
        return getRedirectURL(path);
    }

    @PostMapping("/download/folder")
    public void downloadUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO, HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        response.setHeader("Content-Disposition", "attachment; filename=" + userFolderDTO.getShortName() + ".zip");
        response.setStatus(HttpServletResponse.SC_OK);
        try (OutputStream os = response.getOutputStream();
             ZipOutputStream zos = new ZipOutputStream(os)) {
            userObjectsService.downloadUserFolder(getUserStorageName(), userFolderDTO, zos);
        }
    }

    @PatchMapping("/rename/folder")
    public String renameUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO,
                                   @RequestParam("oldShortUserFolderName") String oldShortUserFolderName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.renameUserFolder(getUserStorageName(), oldShortUserFolderName, userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @DeleteMapping(value = "delete/folder")
    public String deleteUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PostMapping("/download/file")
    public void downloadUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO, HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(userFileDTO.getShortName(), "UTF-8").replace("+", "%20"));
        System.out.println(userFileDTO.getShortName());
        response.setStatus(HttpServletResponse.SC_OK);
        try(OutputStream os = response.getOutputStream()) {
            userObjectsService.downloadUserFile(getUserStorageName(), userFileDTO, os);
        }
    }

    @PatchMapping("/rename/file")
    public String renameUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO,
                                 @RequestParam("oldShortUserFileName") String oldShortUserFileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.renameUserFile(getUserStorageName(), oldShortUserFileName, userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @DeleteMapping(value = "/delete/file")
    public String deleteUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsService.deleteUserFile(getUserStorageName(), userFileDTO);
        return getRedirectURL(userFileDTO);
    }
}
