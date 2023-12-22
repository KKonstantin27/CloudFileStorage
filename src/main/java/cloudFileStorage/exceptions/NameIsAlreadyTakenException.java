package cloudFileStorage.exceptions;

public class NameIsAlreadyTakenException extends RuntimeException {
    public NameIsAlreadyTakenException(String message) {
        super(message);
    }
}
