package cloudFileStorage.configs;

import cloudFileStorage.services.BucketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class MinioBucketConfig implements ApplicationListener<ContextRefreshedEvent> {
    private final BucketsService bucketsService;

    @Autowired
    public MinioBucketConfig(BucketsService bucketsService) {
        this.bucketsService = bucketsService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (!bucketsService.isBucketExists("user-files")) {
                bucketsService.createBucket("user-files");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
