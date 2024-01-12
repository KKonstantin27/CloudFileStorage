package cloudFileStorage.dao;

import cloudFileStorage.exceptions.StorageException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Repository
public class UserObjectsDAO {
    private final MinioClient minioClient;

    @Autowired
    public UserObjectsDAO(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createUserFolder(String newUserFolderName) throws StorageException {
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(newUserFolderName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }

    public boolean uploadUserObject(String userObjectName, MultipartFile userFile) throws StorageException {
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(userObjectName)
                    .stream(userFile.getInputStream(), userFile.getSize(), -1)
                    .contentType(userFile.getContentType())
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
        return true;
    }

    public Iterable<Result<Item>> getUserObjects(String path, boolean isRecursive) throws StorageException {
        Iterable<Result<Item>> results;
        try {
            results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket("user-files")
                    .prefix(path)
                    .startAfter(path)
                    .recursive(isRecursive)
                    .build());
        } catch (Exception e) {
            throw new StorageException();
        }
        return results;
    }

    public void copyUserObject(String oldUserObjectName, String newUserObjectName) throws StorageException {
        try {
            minioClient.copyObject(CopyObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(newUserObjectName)
                    .source(CopySource
                            .builder()
                            .bucket("user-files")
                            .object(oldUserObjectName)
                            .build())
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }

    public InputStream downloadUserObject(String UserFileObject) throws StorageException {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(UserFileObject)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }

    public void deleteUserFolderWithContent(List<DeleteObject> objectsForDeleting) throws StorageException {
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket("user-files")
                .objects(objectsForDeleting)
                .build());
        for (Result<DeleteError> result : results) {
            DeleteError error = null;
            try {
                error = result.get();
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new StorageException();
            }
            System.out.println("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

    public void deleteUserObject(String path) throws StorageException {
        try {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object(path)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }
}
