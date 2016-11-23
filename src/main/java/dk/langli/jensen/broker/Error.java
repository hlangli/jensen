package dk.langli.jensen.broker;

public class Error {
	private Integer code = null;
	private String message = null;
	private Object data = null;
	
	public Error() {
	}
	
	public Error(Integer code, String message, Object data) {
		this();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Object getData() {
		return data;
	}
}
