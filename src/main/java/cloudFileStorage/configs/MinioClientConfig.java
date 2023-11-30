package cloudFileStorage.configs;

import io.minio.MinioClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {
    public MinioClient buildMinioClient() {
        return MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000")
                        .credentials("minio", "password")
                        .build();
    }
}
