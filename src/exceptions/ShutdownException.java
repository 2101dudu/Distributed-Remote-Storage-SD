package exceptions;

public class ShutdownException extends Exception {

    public ShutdownException() {
        super();
    }

    public ShutdownException(String s) {
        super(s);
    }
}
