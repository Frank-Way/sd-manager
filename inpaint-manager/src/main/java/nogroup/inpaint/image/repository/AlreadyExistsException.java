package nogroup.inpaint.image.repository;

public class AlreadyExistsException extends IllegalArgumentException {
    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String s) {
        super(s);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
