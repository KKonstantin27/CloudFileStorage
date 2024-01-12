package cloudFileStorage.configs;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfig {
    @Value("${minio.client-user}")
    private String minioUser;
    @Value("${minio.client-password}")
    private String minioPassword;
    @Value("${minio.client-endpoint}")
    private String minioEndpoint;
    @Bean
    public MinioClient buildMinioClient() {
        System.out.println(minioUser);
        System.out.println(minioEndpoint);
        return MinioClient.builder()
                        .endpoint(minioEndpoint)
                        .credentials(minioUser, minioPassword)
                        .build();
    }
}
