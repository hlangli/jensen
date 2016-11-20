package dk.langli.jensen;

public interface ResponseHandler {
	public JsonRpcResponse onResponse(JsonRpcResponse response) throws JsonRpcException;
}
