package dk.langli.jensen.broker;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

class JsonCause {
    private String exception = null;
    private String message = null;
    private String[] stackTrace = null;
    private JsonCause cause = null;
    private Map<String, Object> data;

    public JsonCause(Throwable target) {
        this(target, null);
    }

    public JsonCause(Throwable target, Map<String, Object> data) {
        this.data = data;
        exception = target.getClass().getName();
        message = target.getMessage();
        StackTraceElement[] stackTrace = target.getStackTrace();
        this.stackTrace = stackTrace != null ? new String[stackTrace.length] : null;
        for(int i = 0; stackTrace != null && i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            this.stackTrace[i] = element.toString();
        }
        Throwable cause = target.getCause();
        this.cause = cause != null ? new JsonCause(cause) : null;
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

    public JsonCause getCause() {
        return cause;
    }

    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }
}
