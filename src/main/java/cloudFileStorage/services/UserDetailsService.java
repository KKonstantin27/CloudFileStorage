package cloudFileStorage.services;

import cloudFileStorage.models.User;
import cloudFileStorage.repositories.UsersRepository;
import cloudFileStorage.security.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UsersRepository usersRepository;

    @Autowired
    public UserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void signUp(User user) {
        usersRepository.save(user);
    }

    public Optional<User> loadUserOptionalByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = usersRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с таким именем не существует");
        }
        return new UserDetails(userOptional.get());
    }
}
