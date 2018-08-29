package dk.langli.jensen.test;

@SuppressWarnings("serial")
public class AugmentedException extends Exception {
	private final String param;

	public AugmentedException(String param) {
		super();
		this.param = param;
	}

	public AugmentedException(String param, String message, Throwable cause) {
		super(message, cause);
		this.param = param;
	}

	public AugmentedException(String param, String message) {
		super(message);
		this.param = param;
	}

	public AugmentedException(String param, Throwable cause) {
		super(cause);
		this.param = param;
	}

	public String getParam() {
		return param;
	}
}
