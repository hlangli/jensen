package dk.langli.jensen.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import dk.langli.jensen.JsonRpcBrokerBuilder;
import dk.langli.jensen.JsonRpcResponse;
import dk.langli.jensen.Request;
import dk.langli.jensen.broker.DefaultSecurityFilter;
import dk.langli.jensen.broker.DefaultSecurityFilter.MatchType;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.JsonRpcException;
import dk.langli.jensen.broker.MethodNotFoundException;
import dk.langli.jensen.broker.ReturnValueHandler;

public class JsonRpcBrokerTest {
    private Logger log = LoggerFactory.getLogger(JsonRpcBrokerTest.class);

    public void voidCall(Object1 object1, int år) {
        log.info(object1.getObject2().getSvend() + " er " + år + " år gammel");
    }

    public Object2 getObject2(Object1 object1, int år) {
        return object1.getObject2();
    }

    public void notification(Object1 object1, int år) {
        log.trace("Testing notify");
    }

    /* @formatter:off */
    @Test
    public void testNotification() throws JsonParseException, JsonMappingException, IOException {
        Request request = not("notification", l(
            m(
                m("integer", 99),
                m("string", "weld"),
                m("object2", m("svend", "grethe"))
            ), 42)
        );
        String responseStr = newJensenBuilder().build().invoke(s(request));
        Assert.assertNull(responseStr);
    }

    /* @formatter:off */
    @Test
    public void testVoidCall() throws JsonProcessingException {
        Request request = req(5, "voidCall", l(
            m(
                m("integer", 99),
                m("string", "weld"),
                m("object2", m("svend", "grethe"))
            ), 42)
        );
        String response = newJensenBuilder().build().invoke(s(request));
        JsonRpcResponse expectedResponse = resp(5, null, null);
        Assert.assertEquals(s(expectedResponse), response);
    }

    /* @formatter:off */
    @Test
    public void testGetObject2() throws JsonProcessingException {
        Request request = req(3, "getObject2", l(
            m(
                m("integer", 99),
                m("string", "weld"),
                m("object2", m("svend", "grethe"))
            ), 42)
        );
        String response = newJensenBuilder().build().invoke(s(request));
        JsonRpcResponse expectedResponse = resp(3, m("svend", "grethe"), null);
        Assert.assertEquals(s(expectedResponse), response);
    }

    /* @formatter:off */
    @Test
    public void testReturnValueHandler() throws JsonProcessingException {
        Request request = req(3, "getObject2", l(
            m(
                m("integer", 99),
                m("string", "weld"),
                m("object2", m("svend", "grethe"))
            ), 42)
        );
        ReturnValueHandler handler = new ReturnValueHandler() {
            public Object onReturnValue(Object returnValue) throws JsonRpcException {
                return "Return value changed to a string";
            }
        };
        JsonRpcBroker jensen = newJensenBuilder().withReturnValueHandler(handler).build();
        String response = jensen.invoke(s(request));
        JsonRpcResponse expectedResponse = resp(3, "Return value changed to a string", null);
        Assert.assertEquals(s(expectedResponse), response);
    }

    /* @formatter:off */
    @Test
    public void testPackagesAreDenied() throws JsonProcessingException {
        JsonRpcBroker jensen = newJensenBuilder().withSecurityFilter(new DefaultSecurityFilter(MatchType.STARTS_WITH, Arrays.asList("dk.langli.jensen.test"))).build();
        Request request = req(4, "java.lang.System", "getenv", null);
        String response = jensen.invoke(s(request));
        Assert.assertTrue("Expected SecurityException", response.contains("java.lang.SecurityException"));
    }

    private JsonRpcBrokerBuilder newJensenBuilder() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return new JsonRpcBrokerBuilder().withObjectMapper(mapper);
    }

    /* @formatter:off */
    @Test
    public void testJodaModule() throws JsonProcessingException {
        JsonRpcBrokerBuilder builder = newJensenBuilder();
        builder = builder.withObjectMapper(new ObjectMapper());
        builder.getObjectMapper().registerModule(new JodaModule());
        builder.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonRpcBroker jensen = builder.build();
        Request request = req(5, UseJoda.class.getName(), "getTheDate", null);
        String response = jensen.invoke(s(request));
        // Jackson will write date/time in UTC timezone
        Assert.assertTrue("Expected ISO8601 date string in response", response.contains("2015-03-26T14:29:00.000Z"));
    }

    /* @formatter:off */
    @SuppressWarnings("unchecked")
    @Test
    public void testMethodIncompatibility() throws JsonParseException, JsonMappingException, IOException {
        Request request = req(3, "getObject2", l(1, 2));
        String responseStr = newJensenBuilder().build().invoke(s(request));
        ObjectMapper mapper = new ObjectMapper();
        JsonRpcResponse response = mapper.readValue(responseStr, JsonRpcResponse.class);
        Assert.assertEquals("Method not found", response.getError().getMessage());
        Map<String, Object> data = mapper.convertValue(response.getError().getData(), Map.class);
        Assert.assertEquals(MethodNotFoundException.class.getName(), data.get("exception"));
        Map<String, Object> incompatible = mapper.convertValue(data.get("incompatible"), Map.class);
        Map<String, Object> getObject2 = mapper.convertValue(incompatible.get("getObject2(Object1, int)"), Map.class);
        Assert.assertEquals(Object1.class.getName(), getObject2.get("parameterType"));
        Assert.assertEquals(0, getObject2.get("index"));
    }
    
    private static JsonRpcResponse resp(Object id, Object result, dk.langli.jensen.broker.Error error) {
        return new JsonRpcResponse(id, result, error);
    }
    
    private static dk.langli.jensen.broker.Error e(Integer code, String message, Object data) {
        return new dk.langli.jensen.broker.Error(code, message, data);
    }

    private static Request not(String method, List<? extends Object> params) {
        return req(null, method, params);
    }

    private static Request req(Object id, String method, List<? extends Object> params) {
        return req(id, JsonRpcBrokerTest.class.getName(), method, params);
    }

    private static Request req(Object id, String className, String method, List<? extends Object> params) {
        return new Request(id, String.format("%s.%s", className, method), params);
    }

    private static List<? extends Object> l(Object... params) {
        return Arrays.asList(params);
    }

    private static Map<String, Object> m(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @SafeVarargs
    private static Map<String, Object> m(Map<String, Object>... maps) {
        Map<String, Object> map = new HashMap<>();
        for(Map<String, Object> m : maps) {
            for(Entry<String, Object> m2 : m.entrySet()) {
                map.put(m2.getKey(), m2.getValue());
            }
        }
        return map;
    }

    private static String s(Object m) throws JsonProcessingException {
        return s(m, new ObjectMapper());
    }

    private static String s(Object m, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.writeValueAsString(m);
    }
}
