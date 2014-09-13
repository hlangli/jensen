package dk.nineconsult.jensen;

public interface ReturnValueHandler {
	public Object onReturnValue(Object returnValue) throws JsonRpcException;
}
