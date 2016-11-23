package dk.langli.jensen.broker;

public interface ReturnValueHandler {
	public Object onReturnValue(Object returnValue) throws JsonRpcException;
}
