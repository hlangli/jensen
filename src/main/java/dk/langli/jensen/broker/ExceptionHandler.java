package dk.langli.jensen.broker;

@FunctionalInterface
public interface ExceptionHandler {
	public Throwable handle(Throwable exception);
}
