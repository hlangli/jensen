package dk.langli.jensen.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.http.JsonRpcHttpServer;
import dk.langli.jensen.caller.JsonRpcCaller;
import dk.langli.jensen.caller.JsonRpcException;
import dk.langli.jensen.caller.TransportException;
import dk.langli.jensen.caller.http.HttpTransport;

public class JsonRpcRoundtripTest {
	private static final String EXCEPTION_MESSAGE = "###EXCEPTION###";
	private final Logger log = LoggerFactory.getLogger(JsonRpcRoundtripTest.class);
	private static String uuid;

    public String getUuid(String uuid) {
        if(JsonRpcRoundtripTest.uuid.equals(uuid)) {
            return uuid;
        }
        else {
            return "NO MATCH";
        }
    }
    
    public void testRoundtrip(String uuid) throws Exception {
        JsonRpcRoundtripTest.uuid = uuid;
    }

    @Test
    public void testRoundtrip() throws Exception {
        JsonRpcBroker broker = JsonRpcBroker.builder().build();
        String loopbackAddress = InetAddress.getLoopbackAddress().getHostAddress();
        InetSocketAddress address = new InetSocketAddress(loopbackAddress, 0);
        JsonRpcHttpServer server = new JsonRpcHttpServer(broker, address);
        server.start();
        HttpTransport transport = new HttpTransport(String.format("http://%s:%s", loopbackAddress, server.getPort()));
        JsonRpcCaller caller = JsonRpcCaller.builder().withTransport(transport).build();
        String uuid = UUID.randomUUID().toString();
        Object result = caller.callThis(uuid);
        Assert.assertNull(result);
        Assert.assertEquals(uuid, JsonRpcRoundtripTest.uuid);
        Assert.assertEquals(String.format("#%s#%s", uuid, uuid), testReturnValue(caller));
        String className = getClass().getName();
        String methodName = "testException";
        try {
        	caller.call(String.format("%s.%s", className, methodName), null, (Object[]) null);
        	Assert.fail();
        }
        catch(Exception e) {
        	Throwable t = e;
        	do {
            	Assert.assertEquals(t.getMessage(), EXCEPTION_MESSAGE);
            	Assert.assertEquals(t.getStackTrace()[0].getClassName(), className);
            	Assert.assertEquals(t.getStackTrace()[0].getMethodName(), methodName);
            	t = t.getCause();
        	}
        	while(t != null);
        }
    }
    
    public void testException() throws Exception {
    	throw new Exception(EXCEPTION_MESSAGE, new NullPointerException(EXCEPTION_MESSAGE));
    }
    
    private String testReturnValue(JsonRpcCaller caller) throws JsonRpcException, TransportException {
        return caller.callThis(uuid);
    }

    public String testReturnValue(String uuid) {
        return String.format("#%s#%s", uuid, JsonRpcRoundtripTest.uuid);
    }
}
