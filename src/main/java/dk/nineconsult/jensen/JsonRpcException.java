package dk.nineconsult.jensen;

@SuppressWarnings("serial")
public class JsonRpcException extends Exception {
	private Error error = null;
	
	public JsonRpcException(Error error) {
		this.error = error;
	}

	public Error getError() {
		return error;
	}
}
