package cloudFileStorage.services;

import cloudFileStorage.models.AppUser;
import cloudFileStorage.repositories.AppUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AppUsersService {
    private final AppUsersRepository appUsersRepository;

    @Autowired
    public AppUsersService(AppUsersRepository appUsersRepository) {
        this.appUsersRepository = appUsersRepository;
    }

    public void signUp(AppUser appUser) {
        appUsersRepository.save(appUser);
    }
}
