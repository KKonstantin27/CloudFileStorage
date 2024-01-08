package cloudFileStorage.controllers;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.exceptions.StorageException;
import cloudFileStorage.services.UserObjectsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Controller
public class UserStorageController extends BaseController {

    private final UserObjectsService userObjectsService;

    @Autowired
    public UserStorageController(UserObjectsService userObjectsService) {
        this.userObjectsService = userObjectsService;
    }

    @GetMapping("/search")
    public String searchUserObjects(@RequestParam("query") String searchQuery, Model model) throws StorageException {
        List<UserObjectDTO> userObjectDTOList = userObjectsService.getUserObjectsBySearchQuery(getUserStorageName(), searchQuery);
        model.addAttribute("userObjectDTOList", userObjectDTOList);
        return "searchResult";
    }

    @PostMapping("/create/folder")
    public String createFolder(@ModelAttribute("userFolderDTO") @Valid UserFolderDTO userFolderDTO,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, StorageException {

        if (bindingResult.hasErrors()) {
            setErrorsRedirectAttribute(bindingResult, redirectAttributes);
            return getRedirectURL(userFolderDTO);
        }

        userObjectsService.createUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PostMapping("/upload/files")
    public String uploadFiles(@RequestParam(value = "path", required = false) String path,
                              @RequestParam("userObject") MultipartFile[] userObjects) throws StorageException, UnsupportedEncodingException {
        userObjectsService.uploadUserFiles(getUserStorageName(), path, userObjects);
        return getRedirectURL(path);
    }

    @PostMapping("/upload/folder")
    public String uploadFolder(@RequestParam(value = "path", required = false) String path,
                               @RequestParam("userObject") MultipartFile[] userObjects) throws StorageException, UnsupportedEncodingException {
        userObjectsService.uploadUserFolder(getUserStorageName(), path, userObjects);
        return getRedirectURL(path);
    }

    @PostMapping("/download/folder")
    public void downloadUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO, HttpServletResponse response) throws StorageException {
        response.setHeader("Content-Disposition", "attachment; filename=" + userFolderDTO.getShortName() + ".zip");
        response.setStatus(HttpServletResponse.SC_OK);
        try (OutputStream os = response.getOutputStream();
             ZipOutputStream zos = new ZipOutputStream(os)) {
            userObjectsService.downloadUserFolder(getUserStorageName(), userFolderDTO, zos);
        } catch (IOException e) {
            throw new StorageException();
        }
    }

    @PatchMapping("/rename/folder")
    public String renameUserFolder(@ModelAttribute("userFolderDTO") @Valid UserFolderDTO userFolderDTO, BindingResult bindingResult,
                                   @RequestParam("oldShortUserFolderName") String oldShortUserFolderName,
                                   RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, StorageException {
        if (bindingResult.hasErrors()) {
            setErrorsRedirectAttribute(bindingResult, redirectAttributes);
            return getRedirectURL(userFolderDTO);
        }
        userObjectsService.renameUserFolder(getUserStorageName(), oldShortUserFolderName, userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @DeleteMapping(value = "/delete/folder")
    public String deleteUserFolder(@ModelAttribute("userFolderDTO") UserFolderDTO userFolderDTO) throws StorageException, UnsupportedEncodingException {
        userObjectsService.deleteUserFolder(getUserStorageName(), userFolderDTO);
        return getRedirectURL(userFolderDTO);
    }

    @PostMapping("/download/file")
    public void downloadUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO, HttpServletResponse response) throws StorageException, UnsupportedEncodingException {
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(userFileDTO.getShortName(), "UTF-8").replace("+", "%20"));
        System.out.println(userFileDTO.getShortName());
        response.setStatus(HttpServletResponse.SC_OK);
        try (OutputStream os = response.getOutputStream()) {
            userObjectsService.downloadUserFile(getUserStorageName(), userFileDTO, os);
        } catch (IOException e) {
            throw new StorageException();
        }
    }

    @PatchMapping("/rename/file")
    public String renameUserFile(@ModelAttribute("userFileDTO") @Valid UserFileDTO userFileDTO, BindingResult bindingResult,
                                 @RequestParam("oldShortUserFileName") String oldShortUserFileName,
                                 RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, StorageException {
        if (bindingResult.hasErrors()) {
            setErrorsRedirectAttribute(bindingResult, redirectAttributes);
            return getRedirectURL(userFileDTO);
        }

        userObjectsService.renameUserFile(getUserStorageName(), oldShortUserFileName, userFileDTO);
        return getRedirectURL(userFileDTO);
    }

    @DeleteMapping(value = "/delete/file")
    public String deleteUserFile(@ModelAttribute("userFileDTO") UserFileDTO userFileDTO) throws StorageException, UnsupportedEncodingException {
        userObjectsService.deleteUserFile(getUserStorageName(), userFileDTO);
        return getRedirectURL(userFileDTO);
    }
}
