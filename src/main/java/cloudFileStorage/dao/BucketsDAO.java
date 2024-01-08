package cloudFileStorage.dao;

import cloudFileStorage.exceptions.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class BucketsDAO {
    private final MinioClient minioClient;

    @Autowired
    public BucketsDAO(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(String bucketName) throws StorageException {
        try {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }

    public boolean isBucketExists(String bucketName) throws StorageException {
        try {
            return minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new StorageException();
        }
    }
}
