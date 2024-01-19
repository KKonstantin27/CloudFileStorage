package cloudFileStorage.utils;

import cloudFileStorage.dao.UserObjectsDAO;
import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserFolderDTO;
import cloudFileStorage.dto.UserObjectDTO;
import cloudFileStorage.exceptions.StorageException;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class UserObjectsUtil {

    private final UserObjectsDAO userObjectsDAO;
    private final static String FOLDER_DELIMITER = "/";

    @Autowired
    public UserObjectsUtil(UserObjectsDAO userObjectsDAO) {
        this.userObjectsDAO = userObjectsDAO;
    }

    public String buildUserObjectPathWithoutStorageName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split(FOLDER_DELIMITER);
        StringBuilder userObjectPath = new StringBuilder();
        for (int i = 1; i < userObjectNameArr.length - 1; i++) {
            userObjectPath.append(userObjectNameArr[i]).append(FOLDER_DELIMITER);
        }
        return userObjectPath.toString();
    }

    public List<UserObjectDTO> sortUserObjectDTOList(List<UserFolderDTO> userFolderDTOList, List<UserFileDTO> userFileDTOList) {
        Comparator<UserObjectDTO> userObjectDTOComparator = Comparator.comparing(UserObjectDTO::getName);
        Collections.sort(userFolderDTOList, userObjectDTOComparator);
        Collections.sort(userFileDTOList, userObjectDTOComparator);
        List<UserObjectDTO> userObjectDTOList = new ArrayList<>();
        userObjectDTOList.addAll(userFolderDTOList);
        userObjectDTOList.addAll(userFileDTOList);
        return userObjectDTOList;
    }

    public String buildUniqueUserObjectName(String userObjectName, String path) throws StorageException {
        StringBuilder userObjectNameSB = new StringBuilder(userObjectName);
        int currentUniqueNum = 0;
        while (isUserObjectNameBusy(userObjectNameSB.toString(), path)) {
            if (currentUniqueNum == 0) {
                userObjectNameSB.append(" (").append(currentUniqueNum + 1).append(")");
            } else {
                userObjectNameSB.replace(userObjectNameSB.length() - 2, userObjectNameSB.length() - 1,
                        currentUniqueNum + 1 + "");
            }
            currentUniqueNum++;
        }
        return userObjectNameSB.toString();
    }

    public boolean isDir(String userObjectName) {
        return userObjectName.endsWith(FOLDER_DELIMITER);
    }

    public String getShortUserObjectName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split(FOLDER_DELIMITER);
        return userObjectNameArr[userObjectNameArr.length - 1];
    }

    public String buildUserObjectNameWithoutStorageName(String userObjectName) {
        String[] userObjectNameArr = userObjectName.split(FOLDER_DELIMITER);
        StringBuilder userObjectNameWithoutStorageName = new StringBuilder();
        for (int i = 1; i < userObjectNameArr.length; i++) {
            if (i == userObjectNameArr.length - 1 && !isDir(userObjectName)) {
                userObjectNameWithoutStorageName.append(userObjectNameArr[i]);
            } else {
                userObjectNameWithoutStorageName.append(userObjectNameArr[i]).append(FOLDER_DELIMITER);
            }
        }
        return userObjectNameWithoutStorageName.toString();
    }

    private boolean isUserObjectNameBusy(String userObjectName, String path) throws StorageException {
        Iterable<Result<Item>> userObjects = userObjectsDAO.getUserObjects(path, false);
        try {
            for (Result<Item> userObject : userObjects) {
                if (getShortUserObjectName(userObject.get().objectName()).equals(getShortUserObjectName(userObjectName))) {
                    return true;
                }
            }
            return false;
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new StorageException();
        }
    }
}
