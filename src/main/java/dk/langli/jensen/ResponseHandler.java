package dk.langli.jensen;

public interface ResponseHandler {
	public Response onResponse(Response response) throws JsonRpcException;
}
