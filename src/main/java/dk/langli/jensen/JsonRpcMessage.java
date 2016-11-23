package dk.langli.jensen;

import dk.langli.jensen.broker.JsonRpcBroker;

abstract class JsonRpcMessage {
	private String jsonrpc = JsonRpcBroker.JSONRPC;
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
