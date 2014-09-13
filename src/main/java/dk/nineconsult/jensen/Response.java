package dk.nineconsult.jensen;

public class Response extends JsonRpcMessage {
	private Object result = null;
	private Error error = null;
	
	public Response() {
		super();
	}

	public Response(Integer id, Object result, Error error) {
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
