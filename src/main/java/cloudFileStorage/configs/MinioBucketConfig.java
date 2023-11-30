package cloudFileStorage.configs;

import cloudFileStorage.services.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MinioBucketConfig implements ApplicationListener<ContextRefreshedEvent> {
    private final BucketService bucketService;

    @Autowired
    public MinioBucketConfig(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (!bucketService.isBucketExists("user-files")) {
                bucketService.createBucket("user-files");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
