package cloudFileStorage.services;

import cloudFileStorage.dao.UserBucketsDAO;
import cloudFileStorage.exceptions.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class BucketsService {
    private final UserBucketsDAO userBucketsDAO;

    @Autowired
    public BucketsService(UserBucketsDAO userBucketsDAO) {
        this.userBucketsDAO = userBucketsDAO;
    }

    public void createBucket(String bucketName) throws StorageException {
        userBucketsDAO.createBucket(bucketName);
    }

    public boolean isBucketExists(String bucketName) throws StorageException {
        return userBucketsDAO.isBucketExists(bucketName);
    }
}
