package dk.nineconsult.jensen;

public interface ResponseHandler {
	public Response onResponse(Response response) throws JsonRpcException;
}
