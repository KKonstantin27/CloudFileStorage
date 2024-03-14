package cloudFileStorage.services;

import cloudFileStorage.dao.BucketsDAO;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional(readOnly = true)
public class BucketsService {
    private final BucketsDAO bucketsDAO;

    @Autowired
    public BucketsService(BucketsDAO bucketsDAO) {
        this.bucketsDAO = bucketsDAO;
    }

    @Transactional
    public void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        bucketsDAO.createBucket(bucketName);
    }

    public boolean isBucketExists(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        return bucketsDAO.isBucketExists(bucketName);
    }
}
