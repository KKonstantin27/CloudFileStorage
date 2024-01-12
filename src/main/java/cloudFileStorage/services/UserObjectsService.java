package cloudFileStorage.services;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.exceptions.StorageException;
import cloudFileStorage.utils.UserObjectsUtil;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class UserObjectsService {
    private final UserObjectsDAO userObjectsDAO;
    private final UserObjectsUtil userObjectsUtil;
    private final static String FOLDER_DELIMITER = "/";

    @Autowired
    public UserObjectsService(UserObjectsDAO userObjectsDAO, UserObjectsUtil userObjectsUtil) {
        this.userObjectsDAO = userObjectsDAO;
        this.userObjectsUtil = userObjectsUtil;
    }

    public void createUserStorage(String userStorageName) throws StorageException {
        userObjectsDAO.createUserFolder(userStorageName + FOLDER_DELIMITER);
    }

    public void createUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws StorageException {
        String path = userFolderDTO.getPath().isEmpty() ? "" : userFolderDTO.getPath();
        String newUserFolderName = userObjectsUtil.buildUniqueUserObjectName(userStorageName + path + userFolderDTO.getShortName(), userStorageName + path) + FOLDER_DELIMITER;
        userObjectsDAO.createUserFolder(newUserFolderName);
    }

    public void uploadUserFolder(String userStorageName, String path, MultipartFile[] userObjects) throws StorageException {
        Path userRootFolderName = Path.of(Arrays.stream(userObjects).findAny().get().getOriginalFilename()).getName(0);
        String uploadUserRootFolderName = userObjectsUtil.buildUniqueUserObjectName(
                userStorageName + path + userRootFolderName,
                userStorageName + path) + FOLDER_DELIMITER;
        userObjectsDAO.createUserFolder(uploadUserRootFolderName);
        for (MultipartFile userObject : userObjects) {
            Path uploadFileName = Path.of(userObject.getOriginalFilename());
            Path uploadFilePath = Path.of(userObject.getOriginalFilename()).getParent();
            StringBuilder uploadFilePathSB = new StringBuilder();
            if (uploadFileName.getNameCount() > 2) {
                for (Path shortUserFolderName : userRootFolderName.relativize(uploadFilePath)) {
                    userObjectsDAO.createUserFolder(uploadUserRootFolderName + uploadFilePathSB.append(shortUserFolderName).append(FOLDER_DELIMITER));
                }
            }
            userObjectsDAO.uploadUserObject(uploadUserRootFolderName + uploadFilePathSB + uploadFileName.getFileName().toString(), userObject);
        }
    }

    public void uploadUserFiles(String userStorageName, String path, MultipartFile[] userFiles) throws StorageException {
        for (MultipartFile userFile : userFiles) {
            String userFileName = userObjectsUtil.buildUniqueUserObjectName(userFile.getOriginalFilename(), userStorageName + path);
            userObjectsDAO.uploadUserObject(userStorageName + path + userFileName, userFile);
        }
    }

    public List<UserObjectDTO> getUserObjects(String userStorageName, String path) throws StorageException {
        List<UserFolderDTO> userFolderDTOList = new ArrayList<>();
        List<UserFileDTO> userFileDTOList = new ArrayList<>();
        path = path == null ? "" : path;
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + path, false);
        try {
            for (Result<Item> userObject : userObjects) {
                String shortUserObjectName = userObjectsUtil.getShortUserObjectName(userObject.get().objectName());
                String userObjectName = userObjectsUtil.buildUserObjectNameWithoutStorageName(userObject.get().objectName());
                String userObjectPath = userObjectsUtil.buildUserObjectPathWithoutStorageName(userObject.get().objectName());
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
            return userObjectsUtil.sortUserObjectDTOList(userFolderDTOList, userFileDTOList);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException();
        }
    }

    public List<UserObjectDTO> getUserObjectsBySearchQuery(String userStorageName, String searchQuery) throws StorageException {
        List<UserFolderDTO> userFolderDTOList = new ArrayList<>();
        List<UserFileDTO> userFileDTOList = new ArrayList<>();
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName, true);
        try {
            for (Result<Item> userObject : userObjects) {
                String userObjectName = userObjectsUtil.buildUserObjectNameWithoutStorageName(userObject.get().objectName());
                String shortUserObjectName = userObjectsUtil.getShortUserObjectName(userObject.get().objectName());
                String userObjectPath = userObjectsUtil.buildUserObjectPathWithoutStorageName(userObject.get().objectName());
                if (searchQuery.equals(shortUserObjectName) && userObjectsUtil.isDir(userObjectName)) {
                    userFolderDTOList.add(new UserFolderDTO(
                            userObjectName,
                            shortUserObjectName,
                            userStorageName,
                            userObjectPath));
                }
                if (searchQuery.equals(shortUserObjectName) && !userObjectsUtil.isDir(userObject.get().objectName())) {
                    userFileDTOList.add(new UserFileDTO(
                            userObjectName,
                            shortUserObjectName,
                            userObject.get().size(),
                            userStorageName,
                            userObjectPath));
                }
            }
            return userObjectsUtil.sortUserObjectDTOList(userFolderDTOList, userFileDTOList);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException();
        }
    }

    public void downloadUserFolder(String userStorageName, UserFolderDTO userFolderDTO, ZipOutputStream zos) throws StorageException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + userFolderDTO.getPath() + userFolderDTO.getShortName() + FOLDER_DELIMITER, true);
        for (Result<Item> userObject : userObjects) {
            try (InputStream is = userObjectsDAO.downloadUserObject(userObject.get().objectName())) {
                zos.putNextEntry(new ZipEntry(userObjectsUtil.buildUserObjectNameWithoutStorageName(userObject.get().objectName())));

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
                zos.closeEntry();
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new StorageException();
            }
        }
    }

    public void renameUserFolder(String userStorageName, String oldShortUserFolderName, UserFolderDTO userFolderDTO) throws StorageException {
        String oldUserFolderName = userStorageName + userFolderDTO.getPath() + oldShortUserFolderName + FOLDER_DELIMITER;
        String newUserFolderName = userObjectsUtil.buildUniqueUserObjectName(userStorageName + userFolderDTO.getPath() + userFolderDTO.getShortName(), userStorageName + userFolderDTO.getPath()) + FOLDER_DELIMITER;
        userObjectsDAO.createUserFolder(newUserFolderName);
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(oldUserFolderName, true);
        try {
            for (Result<Item> userObject : userObjects) {
                String oldUserObjectName = userObject.get().objectName();
                String newUserObjectName = oldUserObjectName.replace(oldUserFolderName, newUserFolderName);
                userObjectsDAO.copyUserObject(oldUserObjectName, newUserObjectName);
                userObjectsDAO.deleteUserObject(oldUserObjectName);
                userObjectsDAO.deleteUserObject(oldUserFolderName);
            }
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException();
        }
    }

    public void deleteUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws StorageException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + userFolderDTO.getName(), true);
        List<DeleteObject> objectsForDeleting = new LinkedList<>();
        try {
            for (Result<Item> userObject : userObjects) {
                objectsForDeleting.add(new DeleteObject(userObject.get().objectName()));
            }
            objectsForDeleting.add(new DeleteObject(userStorageName + userFolderDTO.getName()));
            userObjectsDAO.deleteUserFolderWithContent(objectsForDeleting);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException();
        }
    }

    public void downloadUserFile(String userStorageName, UserFileDTO userFileDTO, OutputStream os) throws StorageException {
        try (InputStream is = userObjectsDAO.downloadUserObject(userStorageName + userFileDTO.getPath() + userFileDTO.getShortName())) {
            FileCopyUtils.copy(is, os);
        } catch (IOException e) {
            throw new StorageException();
        }
    }

    public void renameUserFile(String userStorageName, String oldShortUserFileName, UserFileDTO userFileDTO) throws StorageException {
        String oldUserFileName = userStorageName + userFileDTO.getPath() + oldShortUserFileName;
        String newUserFileName = userObjectsUtil.buildUniqueUserObjectName(userStorageName + userFileDTO.getPath() + userFileDTO.getShortName(), userStorageName + userFileDTO.getPath());
        userObjectsDAO.copyUserObject(oldUserFileName, newUserFileName);
        userObjectsDAO.deleteUserObject(oldUserFileName);
    }

    public void deleteUserFile(String userStorageName, UserFileDTO userFileDTO) throws StorageException {
        userObjectsDAO.deleteUserObject(userStorageName + userFileDTO.getName());
    }









}
