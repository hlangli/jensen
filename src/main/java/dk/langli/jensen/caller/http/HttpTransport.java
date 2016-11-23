package dk.langli.jensen.caller.http;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.langli.jensen.caller.Transport;
import dk.langli.jensen.caller.TransportException;

public class HttpTransport implements Transport {
    private final Logger log = LoggerFactory.getLogger(HttpTransport.class);
    private final String jsonRpcEndpoint;
    
    public HttpTransport(String jsonRpcEndpoint) {
        this.jsonRpcEndpoint = jsonRpcEndpoint;
    }
    
    @Override
    public String send(String jsonRpcRequest) throws TransportException {
        String jsonRpcResponse = null;
        HttpInputStream jsonRpcResponseStream = null;
        try {
            JsonHttp http = new JsonHttp(jsonRpcEndpoint);
            jsonRpcResponseStream = http.post("", jsonRpcRequest);
            jsonRpcResponse = IOUtils.toString(jsonRpcResponseStream);
        }
        catch(IOException e) {
            if(jsonRpcResponseStream != null) {
                try {
                    jsonRpcResponseStream.close();
                }
                catch(IOException e1) {
                    log.warn("Cannot close input stream", e1);
                }
            }
            throw new TransportException(e);
        }
        return jsonRpcResponse;
    }
}
