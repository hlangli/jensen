package dk.langli.jensen.caller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dk.langli.jensen.Request;

public class JsonRpcException extends Exception {
    private static final long serialVersionUID = -218203140871694687L;
    private String exception = null;
    private Request request = null;
    
    public JsonRpcException() {
    	System.out.println("New JsonRpcException");
    }

    public JsonRpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonRpcException(String message) {
		super(message);
	}

	@JsonIgnore
    protected JsonRpcException(Throwable e) {
        super(e.getMessage(), e.getCause());
        setException(e.getClass().getName());
        setStackTrace(e.getStackTrace());
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setStackTrace(String[] stackTrace) {
        List<StackTraceElement> stackTraceProper = new ArrayList<>();
        for(String stackTraceElement: stackTrace) {
            stackTraceProper.add(toStackTraceElement(stackTraceElement));
        }
        super.setStackTrace(stackTraceProper.toArray(new StackTraceElement[stackTraceProper.size()]));
    }
    
    private StackTraceElement toStackTraceElement(String stackTraceElementStr) {
        String declaringClass = stackTraceElementStr.substring(0, stackTraceElementStr.indexOf('('));
        String methodName = declaringClass.substring(declaringClass.lastIndexOf('.')+1);
        declaringClass = declaringClass.substring(0, declaringClass.lastIndexOf('.'));
        String fileName = stackTraceElementStr.substring(stackTraceElementStr.indexOf('(')+1, stackTraceElementStr.indexOf(')'));
        int lineNumber = -1;
        if(fileName.contains(":")) {
            lineNumber = Integer.valueOf(fileName.substring(fileName.indexOf(':')+1));
            fileName = fileName.substring(0, fileName.indexOf(':'));
        }
        else {
            fileName = null;
            lineNumber = -2;
        }
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
    
    @Override
    public String toString() {
        String s = exception;
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
