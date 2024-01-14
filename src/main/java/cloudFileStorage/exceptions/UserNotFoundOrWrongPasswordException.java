package cloudFileStorage.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundOrWrongPasswordException extends UsernameNotFoundException {
    public UserNotFoundOrWrongPasswordException(String message) {
        super(message);
    }
}
