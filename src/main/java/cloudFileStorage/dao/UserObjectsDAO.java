package cloudFileStorage.dao;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket-name}")
    private String bucketName;
    private final MinioClient minioClient;

    @Autowired
    public UserObjectsDAO(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public Iterable<Result<Item>> getUserObjects(String path, boolean isRecursive) {

        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(path)
                .startAfter(path)
                .recursive(isRecursive)
                .build());
    }

    public boolean uploadUserObject(String userObjectName, MultipartFile userFile) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucketName)
                .object(userObjectName)
                .stream(userFile.getInputStream(), userFile.getSize(), -1)
                .contentType(userFile.getContentType())
                .build());

        return true;
    }

    public InputStream downloadUserObject(String UserFileObject) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        return minioClient.getObject(GetObjectArgs
                .builder()
                .bucket(bucketName)
                .object(UserFileObject)
                .build());
    }

    public void copyUserObject(String oldUserObjectName, String newUserObjectName) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        minioClient.copyObject(CopyObjectArgs
                .builder()
                .bucket(bucketName)
                .object(newUserObjectName)
                .source(CopySource
                        .builder()
                        .bucket(bucketName)
                        .object(oldUserObjectName)
                        .build())
                .build());

    }

    public void deleteUserObject(String path) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(bucketName)
                .object(path)
                .build());
    }

    public void createUserFolder(String newUserFolderName) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucketName)
                .object(newUserFolderName)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .build());
    }

    public void deleteUserFolderWithContent(List<DeleteObject> objectsForDeleting) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs
                .builder()
                .bucket(bucketName)
                .objects(objectsForDeleting)
                .build());

        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println("Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }
}
