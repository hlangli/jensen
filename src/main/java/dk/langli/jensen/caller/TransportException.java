package dk.langli.jensen.caller;

@SuppressWarnings("serial")
public class TransportException extends Exception {
    public TransportException() {
        super();
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }
}
