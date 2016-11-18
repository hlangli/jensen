package dk.langli.jensen;

abstract class JsonRpcMessage {
	private String jsonrpc = Jensen.JSONRPC;
	private Object id = null;

	public JsonRpcMessage() {
	}

	public JsonRpcMessage(Object id) {
		this.id = id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public Object getId() {
		return id;
	}
}
