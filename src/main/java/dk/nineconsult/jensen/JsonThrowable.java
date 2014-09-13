package dk.nineconsult.jensen;

class JsonThrowable {
	private String exception = null;
	private String message = null;
	private String[] stackTrace = null;
	private JsonThrowable cause = null;

	public JsonThrowable(Throwable target) {
		exception = target.getClass().getName();
		message = target.getMessage();
		StackTraceElement[] stackTrace = target.getStackTrace();
		this.stackTrace = stackTrace != null ? new String[stackTrace.length] : null;
		for(int i = 0; stackTrace != null && i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			this.stackTrace[i] = element.toString();
		}
		Throwable cause = target.getCause();
		this.cause = cause != null ? new JsonThrowable(cause) : null;
	}

	public String getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String[] getStackTrace() {
		return stackTrace;
	}

	public JsonThrowable getCause() {
		return cause;
	}
}
