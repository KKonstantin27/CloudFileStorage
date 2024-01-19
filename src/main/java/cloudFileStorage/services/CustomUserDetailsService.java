package cloudFileStorage.services;

import cloudFileStorage.enums.UserRoles;
import cloudFileStorage.exceptions.UserNotFoundOrWrongPasswordException;
import cloudFileStorage.models.User;
import cloudFileStorage.repositories.UsersRepository;
import cloudFileStorage.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRoles.USER.getUserRole());
        return usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> loadUserOptionalByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = usersRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundOrWrongPasswordException("Wrong username or password");
        }
        return new CustomUserDetails(userOptional.get());
    }
}
