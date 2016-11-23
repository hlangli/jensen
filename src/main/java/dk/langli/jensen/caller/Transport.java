package dk.langli.jensen.caller;

public interface Transport {
    public String send(String jsonRpcRequest) throws TransportException;
}
