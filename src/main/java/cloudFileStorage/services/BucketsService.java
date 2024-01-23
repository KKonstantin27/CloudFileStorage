package cloudFileStorage.services;

import cloudFileStorage.dao.BucketsDAO;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class BucketsService {
    private final BucketsDAO bucketsDAO;

    @Autowired
    public BucketsService(BucketsDAO bucketsDAO) {
        this.bucketsDAO = bucketsDAO;
    }

    public void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        bucketsDAO.createBucket(bucketName);
    }

    public boolean isBucketExists(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        return bucketsDAO.isBucketExists(bucketName);
    }
}
