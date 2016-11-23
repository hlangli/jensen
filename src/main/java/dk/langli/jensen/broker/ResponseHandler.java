package dk.langli.jensen.broker;

import dk.langli.jensen.JsonRpcResponse;

public interface ResponseHandler {
	public JsonRpcResponse onResponse(JsonRpcResponse response) throws JsonRpcException;
}
