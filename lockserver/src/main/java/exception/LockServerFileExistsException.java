package exception;

public class LockServerFileExistsException extends RuntimeException {
    public LockServerFileExistsException(String message) {
        super(message);
    }

    public LockServerFileExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
