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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserObjectsService {
    private final UserObjectsDAO userObjectsDAO;

    @Autowired
    public UserObjectsService(UserObjectsDAO userObjectsDAO) {
        this.userObjectsDAO = userObjectsDAO;
    }

    public void createUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path = userFolderDTO.getPath().isEmpty() ? "" : userFolderDTO.getPath();
        String newUserFolderName = userStorageName + path + userFolderDTO.getShortName() + "/";
        checkBusyUserObjectNames(userStorageName, path, userFolderDTO.getShortName());
        userObjectsDAO.createUserFolder(newUserFolderName);
    }

    public void createUserStorage(String userStorageName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.createUserFolder(userStorageName + "/");
    }

//    public void uploadUserObject(String path, MultipartFile userObject) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        minioClient.putObject(PutObjectArgs
//                .builder()
//                .bucket("user-files")
//                .object(path + userObject.getOriginalFilename())
//                .stream(userObject.getInputStream(), userObject.getSize(), -1)
//                .contentType(userObject.getContentType())
//                .build());
//    }

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

//    private List<UserObjectDTO> getUserFoldersBySearchQuery(String path, String searchQuery, List<UserObjectDTO> userObjectDTOList, Queue<String> foldersQueue) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        Iterable<Result<Item>> userFolders = userObjectsDAO.getUserObjects(path, false);
//        for (Result<Item> userFolder : userFolders) {
//            System.out.println(userFolder.get().objectName());
//            String userObjectPath = buildUserFolderPath(userFolder.get().objectName());
//            String userObjectName = getUserObjectName(userFolder.get().objectName());
//            if (isDir(userFolder.get().objectName())) {
//                foldersQueue.add(userFolder.get().objectName());
//                if (searchQuery.equals(userObjectName)) {
//                    userObjectDTOList.add(new UserFolderDTO(userFolder.get().objectName(), userFolder.get().size(), path, userObjectPath));
//                }
//            }
//        }
//        if (!foldersQueue.isEmpty()) {
//            userObjectDTOList = getUserFoldersBySearchQuery(foldersQueue.poll(), searchQuery, userObjectDTOList, foldersQueue);
//        }
//        return userObjectDTOList;
//    }

    public void downloadUserFile(String userStorageName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.downloadUserFile(userFileDTO.getName(), userStorageName + userFileDTO.getPath() + userFileDTO.getName());
    }
    public void renameUserFolder(String userStorageName, String oldShortUserFolderName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldUserFileName = userStorageName + userFolderDTO.getPath() + oldShortUserFolderName;
        String newUserFileName = userFolderDTO.getName();
        checkBusyUserObjectNames(userStorageName, userFolderDTO.getPath(), newUserFileName);
        userObjectsDAO.copyUserObject(oldUserFileName, newUserFileName);
        userObjectsDAO.deleteUserObject(oldUserFileName);
    }

    public void renameUserFile(String userStorageName, String oldShortUserFileName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldUserFileName = userStorageName + userFileDTO.getPath() + oldShortUserFileName;
        String newUserFileName = userStorageName + userFileDTO.getPath() + userFileDTO.getShortName();
        checkBusyUserObjectNames(userStorageName, userFileDTO.getPath(), userFileDTO.getShortName());
        userObjectsDAO.copyUserObject(oldUserFileName, newUserFileName);
        userObjectsDAO.deleteUserObject(oldUserFileName);
    }

    public void deleteUserFile (String userStorageName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.deleteUserObject(userStorageName + userFileDTO.getName());
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

    private void checkBusyUserObjectNames(String userStorageName, String path, String newUserObjectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + path, false);
        for (Result<Item> userObject : userObjects) {
            if (getShortUserObjectName(userObject.get().objectName()).equals(newUserObjectName)) {
                throw new NameIsAlreadyTakenException("This name is already taken");
            }
        }
    }

    private boolean isDir(String userObjectName) {
        return userObjectName.endsWith("/");
    }
}
