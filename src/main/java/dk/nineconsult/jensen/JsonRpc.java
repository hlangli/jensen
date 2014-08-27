package dk.nineconsult.jensen;

abstract class JsonRpc {
	private String jsonrpc = Jensen.JSONRPC;
	private Integer id = null;

	public JsonRpc() {
	}

	public JsonRpc(Integer id) {
		this.id = id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public Integer getId() {
		return id;
	}
}
