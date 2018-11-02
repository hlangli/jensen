package dk.langli.jensen.caller.http;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.langli.jensen.caller.Transport;
import dk.langli.jensen.caller.TransportException;

public class HttpTransport implements Transport {
    private final Logger log = LoggerFactory.getLogger(HttpTransport.class);
    private final String jsonRpcEndpoint;
    private final String username;
    private final String password;
    
    public HttpTransport(String jsonRpcEndpoint) {
   	 this(jsonRpcEndpoint, null, null);
    }

    public HttpTransport(String jsonRpcEndpoint, String username, String password) {
        this.jsonRpcEndpoint = jsonRpcEndpoint;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public String send(String jsonRpcRequest) throws TransportException {
        String jsonRpcResponse = null;
        HttpInputStream jsonRpcResponseStream = null;
        try {
            JsonHttp http = new JsonHttp(jsonRpcEndpoint);
            if(username != null) {
            	http.addAuthentication(username, password);
            }
            jsonRpcResponseStream = http.post("", jsonRpcRequest);
            jsonRpcResponse = IOUtils.toString(jsonRpcResponseStream, Charset.defaultCharset());
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
