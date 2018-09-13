package dk.langli.jensen.broker;

@FunctionalInterface
public interface ExceptionUnwrapFilter {
	public boolean isUnwrappable(Class<? extends Throwable> exception);
}
