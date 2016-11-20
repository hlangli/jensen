package dk.langli.jensen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JsonRpcResponse extends JsonRpcMessage {
	@JsonInclude(Include.NON_NULL)
	private Error error = null;
	@JsonInclude(Include.NON_NULL)
	private Object result = null;

	public JsonRpcResponse() {
		super();
	}

	public JsonRpcResponse(Object id, Object result, Error error) {
		super(id);
		this.result = result;
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public Error getError() {
		return error;
	}
}
