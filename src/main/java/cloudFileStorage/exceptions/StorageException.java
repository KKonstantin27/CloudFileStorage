package cloudFileStorage.exceptions;

public class StorageException extends Exception {
    private final static String message = "Internal Server Error. Try again later or contact your administrator";

    public StorageException() {
        super(message);
    }
}
