package cloudFileStorage.services;

import cloudFileStorage.dto.UserObjectDTO;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserObjectsService {
    private final MinioClient minioClient;

    @Autowired
    public UserObjectsService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createFolder(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(path).stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                .build());
    }

    public List<UserObjectDTO> getObjects(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserObjectDTO> userObjectDTOList = new ArrayList<>();

        Iterable<Result<Item>> userObjects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("user-files")
                .startAfter(path)
                .prefix(path)
                .build());

        for (Result<Item> userObject : userObjects) {
            String userObjectName = getUserObjectName(userObject.get().objectName().split("/"));
            if (userObject.get().isDir()) {
                userObjectDTOList.add(new UserObjectDTO(userObjectName, userObject.get().size(), true));
            } else {
                userObjectDTOList.add(new UserObjectDTO(userObjectName, userObject.get().size(), false));
            }
        }
        Collections.reverse(userObjectDTOList);
        return userObjectDTOList;
    }

    public String getUserObjectName(String[] userObjectPath) {
        return userObjectPath[userObjectPath.length - 1];
    }

    public Map<String, String> buildBreadcrumbs(String userStorageName, String path) {
        Map<String, String> breadcrumbs = new LinkedHashMap<>();
        String[] pathArr = path.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (String fragmentOfPath : pathArr) {
            breadcrumbs.put(fragmentOfPath + "/", (currentPath.append(fragmentOfPath).append("/")).toString());
        }
        return breadcrumbs;
    }
}
