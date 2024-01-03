package cloudFileStorage.services;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.exceptions.NameIsAlreadyTakenException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.val;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class UserObjectsService {
    private final UserObjectsDAO userObjectsDAO;

    @Autowired
    public UserObjectsService(UserObjectsDAO userObjectsDAO) {
        this.userObjectsDAO = userObjectsDAO;
    }

    public void createUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path = userFolderDTO.getPath().isEmpty() ? "" : userFolderDTO.getPath();
        String newUserFolderName = buildUniqueUserObjectName(userStorageName + path + userFolderDTO.getShortName(), userStorageName + path) + "/";
        userObjectsDAO.createUserFolder(newUserFolderName);
    }

    public void uploadUserObjects(String userStorageName, String path, MultipartFile[] userObjects) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (MultipartFile userObject : userObjects) {
            userObjectsDAO.uploadUserObject(userStorageName + path, userObject);
        }
    }

    public List<UserObjectDTO> getUserObjects(String userStorageName, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserFolderDTO> userFolderDTOList = new ArrayList<>();
        List<UserFileDTO> userFileDTOList = new ArrayList<>();
        path = path == null ? "" : path;
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + path, false);

        for (Result<Item> userObject : userObjects) {
            String shortUserObjectName = getShortUserObjectName(userObject.get().objectName());
            String userObjectName = buildUserObjectNameWithoutStorageName(userObject.get().objectName());
            String userObjectPath = buildUserObjectPathWithoutStorageName(userObject.get().objectName());
            if (userObject.get().isDir()) {
                userFolderDTOList.add(new UserFolderDTO(
                        userObjectName,
                        shortUserObjectName,
                        userStorageName,
                        userObjectPath));
            } else {
                userFileDTOList.add(new UserFileDTO(
                        userObjectName,
                        shortUserObjectName,
                        userObject.get().size(),
                        userStorageName,
                        userObjectPath));
            }
        }
//TODO STREAM API
        return sortUserObjectDTOList(userFolderDTOList, userFileDTOList);
    }

    public List<UserObjectDTO> getUserObjectsBySearchQuery(String userStorageName, String searchQuery) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserFolderDTO> userFolderDTOList = new ArrayList<>();
        List<UserFileDTO> userFileDTOList = new ArrayList<>();
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName, true);
        for (Result<Item> userObject : userObjects) {
            String userObjectName = buildUserObjectNameWithoutStorageName(userObject.get().objectName());
            String shortUserObjectName = getShortUserObjectName(userObject.get().objectName());
            String userObjectPath = buildUserObjectPathWithoutStorageName(userObject.get().objectName());
            if (searchQuery.equals(shortUserObjectName) && isDir(userObjectName)) {
                userFolderDTOList.add(new UserFolderDTO(
                        userObjectName,
                        shortUserObjectName,
                        userStorageName,
                        userObjectPath));
            }
            if (searchQuery.equals(shortUserObjectName) && !isDir(userObject.get().objectName())) {
                userFileDTOList.add(new UserFileDTO(
                        userObjectName,
                        shortUserObjectName,
                        userObject.get().size(),
                        userStorageName,
                        userObjectPath));
            }
        }
//TODO STREAM API
        return sortUserObjectDTOList(userFolderDTOList, userFileDTOList);
    }

    public void downloadUserFolder(String userStorageName, UserFolderDTO userFolderDTO, ZipOutputStream zos) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + userFolderDTO.getPath() + userFolderDTO.getShortName() + "/", true);
        for (Result<Item> userObject : userObjects) {
            try(InputStream is = userObjectsDAO.downloadUserFolder(userObject.get().objectName())) {
                System.out.println(buildUserObjectNameWithoutStorageName(userObject.get().objectName()));
                zos.putNextEntry(new ZipEntry(buildUserObjectNameWithoutStorageName(userObject.get().objectName())));

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
                zos.closeEntry();
            }
        }
    }

    public void renameUserFolder(String userStorageName, String oldShortUserFolderName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldUserFolderName = userStorageName + userFolderDTO.getPath() + oldShortUserFolderName + "/";
        String newUserFolderName = buildUniqueUserObjectName(userStorageName + userFolderDTO.getPath() + userFolderDTO.getShortName(), userStorageName + userFolderDTO.getPath()) + "/";
        userObjectsDAO.createUserFolder(newUserFolderName);
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(oldUserFolderName, true);
        for (Result<Item> userObject : userObjects) {
            String oldUserObjectName = userObject.get().objectName();
            String newUserObjectName = oldUserObjectName.replace(oldUserFolderName, newUserFolderName);
            userObjectsDAO.copyUserObject(oldUserObjectName, newUserObjectName);
            userObjectsDAO.deleteUserObject(oldUserObjectName);
        }
        userObjectsDAO.deleteUserObject(oldUserFolderName);
    }

    public void deleteUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + userFolderDTO.getName(),true);
        List<DeleteObject> objectsForDeleting = new LinkedList<>();
        for (Result<Item> userObject : userObjects) {
            objectsForDeleting.add(new DeleteObject(userObject.get().objectName()));
        }
        objectsForDeleting.add(new DeleteObject(userStorageName + userFolderDTO.getName()));
        userObjectsDAO.deleteUserFolderWithContent(objectsForDeleting);
    }

    public void downloadUserFile(String userStorageName, UserFileDTO userFileDTO, OutputStream os) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try(InputStream is = userObjectsDAO.downloadUserFolder(userStorageName + userFileDTO.getPath() + userFileDTO.getShortName())) {
            FileCopyUtils.copy(is, os);
        }
    }

    public void renameUserFile(String userStorageName, String oldShortUserFileName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldUserFileName = userStorageName + userFileDTO.getPath() + oldShortUserFileName;
        String newUserFileName = buildUniqueUserObjectName(userStorageName + userFileDTO.getPath() + userFileDTO.getShortName(), userStorageName + userFileDTO.getPath());
        userObjectsDAO.copyUserObject(oldUserFileName, newUserFileName);
        userObjectsDAO.deleteUserObject(oldUserFileName);
    }

    public void deleteUserFile (String userStorageName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.deleteUserObject(userStorageName + userFileDTO.getName());
    }

    public Map<String, String> buildBreadcrumbs(String userStorageName, String path) {
        Map<String, String> breadcrumbs = new LinkedHashMap<>();
        breadcrumbs.put("", userStorageName);

        if (path == null) {
            return breadcrumbs;
        }

        String[] pathArr = path.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (String fragmentOfPath : pathArr) {
            breadcrumbs.put((currentPath.append(fragmentOfPath).append("/")).toString(), fragmentOfPath + "/");
        }
        return breadcrumbs;
    }

    public void createUserStorage(String userStorageName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.createUserFolder(userStorageName + "/");
    }

    private String getShortUserObjectName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split("/");
        return userObjectNameArr[userObjectNameArr.length - 1];
    }

    private String buildUserObjectNameWithoutStorageName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split("/");
        StringBuilder userObjectNameWithoutStorageName = new StringBuilder();
        for (int i = 1; i < userObjectNameArr.length; i++) {
            if (i == userObjectNameArr.length - 1 && !isDir(userObjectName)) {
                userObjectNameWithoutStorageName.append(userObjectNameArr[i]);
            } else {
                userObjectNameWithoutStorageName.append(userObjectNameArr[i]).append("/");
            }
        }
        return userObjectNameWithoutStorageName.toString();
    }

    private String buildUserObjectPathWithoutStorageName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split("/");
        StringBuilder userObjectPath = new StringBuilder();
        for (int i = 1; i < userObjectNameArr.length - 1; i++) {
            userObjectPath.append(userObjectNameArr[i]).append("/");
        }
        return userObjectPath.toString();
    }

    private List<UserObjectDTO> sortUserObjectDTOList(List<UserFolderDTO> userFolderDTOList, List<UserFileDTO> userFileDTOList) {
        Comparator<UserObjectDTO> userObjectDTOComparator = Comparator.comparing(UserObjectDTO::getName);
        Collections.sort(userFolderDTOList, userObjectDTOComparator);
        Collections.sort(userFileDTOList, userObjectDTOComparator);
        List<UserObjectDTO> userObjectDTOList = new ArrayList<>();
        userObjectDTOList.addAll(userFolderDTOList);
        userObjectDTOList.addAll(userFileDTOList);
        return userObjectDTOList;
    }

    private boolean isUserObjectNameBusy(String userObjectName, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(path, false);
        for (Result<Item> userObject : userObjects) {
            if (getShortUserObjectName(userObject.get().objectName()).equals(getShortUserObjectName(userObjectName))) {
                return true;
            }
        }
        return false;
    }

    public String buildUniqueUserObjectName(String userObjectName, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StringBuilder userObjectNameSB = new StringBuilder(userObjectName);
        int currentUniqueNum = 0;
        while (isUserObjectNameBusy(userObjectNameSB.toString(), path)) {
            if (currentUniqueNum == 0) {
                userObjectNameSB.append(" (").append(currentUniqueNum + 1).append(")");
            } else {
                userObjectNameSB.replace(userObjectNameSB.length() - 2, userObjectNameSB.length() - 1, currentUniqueNum + 1 + "");
            }
            currentUniqueNum++;
        }
        return userObjectNameSB.toString();
    }

    private boolean isDir(String userObjectName) {
        return userObjectName.endsWith("/");
    }

}
