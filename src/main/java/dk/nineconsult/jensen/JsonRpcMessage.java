package dk.nineconsult.jensen;

abstract class JsonRpcMessage {
	private String jsonrpc = Jensen.JSONRPC;
	private Integer id = null;

	public JsonRpcMessage() {
	}

	public JsonRpcMessage(Integer id) {
		this.id = id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public Integer getId() {
		return id;
	}
}
