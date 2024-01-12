package cloudFileStorage.configs;

import cloudFileStorage.services.BucketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MinioBucketConfig implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${minio.bucket-name}")
    private String bucketName;

    private final BucketsService bucketsService;
    @Autowired
    public MinioBucketConfig(BucketsService bucketsService) {
        this.bucketsService = bucketsService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (!bucketsService.isBucketExists(bucketName)) {
                bucketsService.createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
