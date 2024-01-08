package cloudFileStorage.services;

import cloudFileStorage.dao.BucketsDAO;
import cloudFileStorage.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BucketsService {
    private final BucketsDAO bucketsDAO;

    @Autowired
    public BucketsService(BucketsDAO bucketsDAO) {
        this.bucketsDAO = bucketsDAO;
    }

    public void createBucket(String bucketName) throws StorageException {
        bucketsDAO.createBucket(bucketName);
    }

    public boolean isBucketExists(String bucketName) throws StorageException {
        return bucketsDAO.isBucketExists(bucketName);
    }
}
