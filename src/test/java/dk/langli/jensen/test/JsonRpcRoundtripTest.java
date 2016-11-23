package dk.langli.jensen.test;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import dk.langli.jensen.JsonRpcBrokerBuilder;
import dk.langli.jensen.JsonRpcCallerBuilder;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.http.JsonRpcHttpServer;
import dk.langli.jensen.caller.JsonRpcCaller;
import dk.langli.jensen.caller.http.HttpTransport;

public class JsonRpcRoundtripTest {
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
        JsonRpcBroker broker = new JsonRpcBrokerBuilder().build();
        JsonRpcHttpServer server = new JsonRpcHttpServer(broker);
        server.start();
        HttpTransport transport = new HttpTransport(String.format("http://localhost:%s/", server.getPort()));
        JsonRpcCaller caller = new JsonRpcCallerBuilder().withTransport(transport).build();
        String uuid = UUID.randomUUID().toString();
        Object result = caller.callThis(uuid);
        Assert.assertNull(result);
        Assert.assertEquals(uuid, JsonRpcRoundtripTest.uuid);
    }
}
