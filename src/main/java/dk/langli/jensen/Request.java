package dk.langli.jensen;

import java.util.List;

public class Request extends JsonRpcMessage {
	private String method = null;
	private List<? extends Object> params = null;
	
	public Request() {
		super();
	}

	public Request(Object id, String method, List<? extends Object> params) {
		super(id);
		this.method = method;
		this.params = params;
	}

	public String getMethod() {
		return method;
	}

	public List<? extends Object> getParams() {
		return params;
	}
}
