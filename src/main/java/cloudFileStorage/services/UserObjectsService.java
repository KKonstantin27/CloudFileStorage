package cloudFileStorage.services;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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

    public void createFolder(String userStorageName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path = userFolderDTO.getPath().isEmpty() ? "" : userFolderDTO.getPath();
        userObjectsDAO.createUserFolder(userStorageName + path + userFolderDTO.getName() + "/");
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
            String userObjectName = getUserObjectName(userObject.get().objectName());
            String currentPath = userObject.get().objectName();
            if (userObject.get().isDir()) {
                userFolderDTOList.add(new UserFolderDTO(
                        userObjectName,
                        userObject.get().size(),
                        userStorageName,
                        buildUserFolderPath(currentPath)));
            } else {
                userFileDTOList.add(new UserFileDTO(
                        userObjectName,
                        userObject.get().size(),
                        userStorageName,
                        userObject.get().objectName()));
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
            String userObjectName = getUserObjectName(userObject.get().objectName());
            if (searchQuery.equals(userObjectName) && isDir(userObject.get().objectName())) {
                userFolderDTOList.add(new UserFolderDTO(
                        userObject.get().objectName(),
                        userObject.get().size(),
                        userStorageName,
                        buildUserFolderPath(userObject.get().objectName())));
            }
            if (searchQuery.equals(userObjectName) && !isDir(userObject.get().objectName())) {
                userFileDTOList.add(new UserFileDTO(
                        userObject.get().objectName(),
                        userObject.get().size(),
                        userStorageName,
                        buildUserFolderPath(userObject.get().objectName())));
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

    public void renameUserFile(String userStorageName, String oldUserFileName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String oldPath = userStorageName + userFileDTO.getPath() + oldUserFileName;
        String newPath = userStorageName + userFileDTO.getPath() + userFileDTO.getName();
        userObjectsDAO.copyUserObject(oldPath, newPath);
        userObjectsDAO.deleteUserObject(oldPath);
    }

    public void deleteUserFile (String userStorageName, UserFileDTO userFileDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.deleteUserObject(userStorageName + userFileDTO.getPath() + userFileDTO.getName());
    }

    public void deleteUserFolder(String userStorageName, UserFolderDTO userFolderDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(userStorageName + userFolderDTO.getPath() + userFolderDTO.getName() + "/",true);
        List<DeleteObject> objectsForDeleting = new LinkedList<>();
        for (Result<Item> userObject : userObjects) {
            objectsForDeleting.add(new DeleteObject(userObject.get().objectName()));
        }
        objectsForDeleting.add(new DeleteObject(userStorageName + userFolderDTO.getPath() + userFolderDTO.getName() + "/"));
        userObjectsDAO.deleteUserFolderWithContent(objectsForDeleting);
    }

    private String getUserObjectName(String userObjectPath) {
        String[] userObjectPathArr = userObjectPath.split("/");
        return userObjectPathArr[userObjectPathArr.length - 1];
    }

    private String buildUserFolderPath(String path) {
        String[] userObjectPathArr = path.split("/");
        StringBuilder userObjectPath = new StringBuilder();
        int userObjectPathEnd = userObjectPathArr.length;
        if (!isDir(path)) {
            userObjectPathEnd = userObjectPathArr.length - 1;
        }
        for (int i = 1; i < userObjectPathEnd; i++) {
            userObjectPath.append(userObjectPathArr[i]).append("/");
        }
        return userObjectPath.toString();
    }

    private boolean isDir(String fullUserObjectName) {
        return fullUserObjectName.endsWith("/");
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

    private List<UserObjectDTO> sortUserObjectDTOList(List<UserFolderDTO> userFolderDTOList, List<UserFileDTO> userFileDTOList) {
        Comparator<UserObjectDTO> userObjectDTOComparator = Comparator.comparing(UserObjectDTO::getName);
        Collections.sort(userFolderDTOList, userObjectDTOComparator);
        Collections.sort(userFileDTOList, userObjectDTOComparator);
        List<UserObjectDTO> userObjectDTOList = new ArrayList<>();
        userObjectDTOList.addAll(userFolderDTOList);
        userObjectDTOList.addAll(userFileDTOList);
        return userObjectDTOList;
    }

}
