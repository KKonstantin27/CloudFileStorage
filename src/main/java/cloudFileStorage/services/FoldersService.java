package cloudFileStorage.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FoldersService {
    private final MinioClient minioClient;

    @Autowired
    public FoldersService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createFolder(String folderPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(folderPath).stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                .build());
    }
}
