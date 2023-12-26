package cloudFileStorage.dao;

import cloudFileStorage.dto.UserFileDTO;
import cloudFileStorage.dto.UserObjectDTO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserObjectsDAO {
    private final MinioClient minioClient;

    @Autowired
    public UserObjectsDAO(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createUserFolder(String newUserFolderName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(newUserFolderName)
                .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                .build());
    }

    public void uploadUserObject(String path, MultipartFile userObject) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(path + userObject.getOriginalFilename())
                .stream(userObject.getInputStream(), userObject.getSize(), -1)
                .contentType(userObject.getContentType())
                .build());
    }

    public Iterable<Result<Item>> getUserObjects(String path, boolean isRecursive) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("user-files")
                .prefix(path)
                .startAfter(path)
                .recursive(isRecursive)
                .build());
    }

    public Iterable<Result<Item>> getUserFolders(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("user-files")
                .prefix(path)
                .delimiter("/")
                .build());
    }

    public void copyUserObject(String oldPath, String newPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.copyObject(CopyObjectArgs
                .builder()
                .bucket("user-files")
                .object(newPath)
                .source(CopySource
                        .builder()
                        .bucket("user-files")
                        .object(oldPath)
                        .build())
                .build());
    }

    public void downloadUserFile(String shortUserFileName, String UserFileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.downloadObject(DownloadObjectArgs
                .builder()
                .bucket("user-files")
                .object(UserFileName)
                .filename(shortUserFileName)
                .build());
    }
    public void deleteUserObject(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket("user-files")
                .object(path)
                .build());
    }

    public void deleteUserFolderWithContent(List<DeleteObject> objectsForDeleting) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket("user-files")
                .objects(objectsForDeleting)
                .build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }
}
