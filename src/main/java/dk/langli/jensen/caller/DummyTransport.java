package dk.langli.jensen.caller;

class DummyTransport implements Transport {
    @Override
    public String send(String jsonRpcRequest) throws TransportException {
        throw new TransportException("No transport");
    }
}
