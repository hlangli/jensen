package dk.nineconsult.jensen;

import java.util.List;

public class Request extends JsonRpcMessage {
	private String method = null;
	private List<Object> params = null;
	
	public Request() {
		super();
	}

	public Request(Integer id, String method, List<Object> params) {
		super(id);
		this.method = method;
		this.params = params;
	}

	public String getMethod() {
		return method;
	}

	public List<Object> getParams() {
		return params;
	}
}
