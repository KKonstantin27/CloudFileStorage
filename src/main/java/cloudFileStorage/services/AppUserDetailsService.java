package cloudFileStorage.services;

import cloudFileStorage.models.AppUser;
import cloudFileStorage.repositories.AppUsersRepository;
import cloudFileStorage.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AppUserDetailsService extends UserDetailsService {
    private final AppUsersRepository appUsersRepository;

    @Autowired
    public AppUserDetailsService(AppUsersRepository appUsersRepository) {
        this.appUsersRepository = appUsersRepository;
    }

    public void signUp(AppUser appUser) {
        appUsersRepository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUserOptional = appUsersRepository.findByUsername(username);
        if (appUserOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с таким именем не существует");
        }
        return new AppUserDetails(appUserOptional.get());
    }
}
