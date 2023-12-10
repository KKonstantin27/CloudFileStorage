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

    public void createFolder(String userStorageName, String path, String userFolderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        path = path.isEmpty() ? "" : path;
        userObjectsDAO.createFolder(userStorageName + path + userFolderName + "/");
    }

    public void createUserStorage(String userStorageName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.createFolder(userStorageName + "/");
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

    public List<UserObjectDTO> getObjects(String userStorageName, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserObjectDTO> userObjectDTOList = new ArrayList<>();
        path = path == null ? "" : path;
        Iterable<Result<Item>> userObjects = userObjectsDAO.getObjects(userStorageName + path, false);

        for (Result<Item> userObject : userObjects) {
            String userObjectName = getUserObjectName(userObject.get().objectName().split("/"));
            if (userObject.get().isDir()) {
                userObjectDTOList.add(new UserFolderDTO(userObjectName, userObject.get().size(), userStorageName, path));
            } else {
                userObjectDTOList.add(new UserFileDTO(userObjectName, userObject.get().size(), userStorageName, path));
            }
        }

        Collections.reverse(userObjectDTOList);
        return userObjectDTOList;
    }

    public void deleteUserFile (String userStorageName, String path, String userFileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        userObjectsDAO.deleteUserObject(userStorageName + path + userFileName);
    }

    public void deleteUserFolder(String userStorageName, String path, String userFolderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getObjects(userStorageName + path + userFolderName,true);
        List<DeleteObject> objectsForDeleting = new LinkedList<>();
        for (Result<Item> userObject : userObjects) {
            objectsForDeleting.add(new DeleteObject(userObject.get().objectName()));
        }
        if (objectsForDeleting.isEmpty()) {
            userObjectsDAO.deleteUserObject(userStorageName + path + userFolderName + "/");
        } else {
            userObjectsDAO.deleteUserFolderWithContent(objectsForDeleting);
        }
    }

    public String getUserObjectName(String[] userObjectPath) {
        return userObjectPath[userObjectPath.length - 1];
    }

    public Map<String, String> buildBreadcrumbs(String userStorageName, String path) {

        if (path == null) {
            return null;
        }

        Map<String, String> breadcrumbs = new LinkedHashMap<>();
        String[] pathArr = path.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (String fragmentOfPath : pathArr) {
            breadcrumbs.put(fragmentOfPath + "/", (currentPath.append(fragmentOfPath).append("/")).toString());
        }
        return breadcrumbs;
    }
}
