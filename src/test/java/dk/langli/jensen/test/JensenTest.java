package dk.langli.jensen.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import dk.langli.jensen.DefaultPrettyPrinter;
import dk.langli.jensen.DefaultSecurityFilter;
import dk.langli.jensen.DefaultSecurityFilter.MatchType;
import dk.langli.jensen.Jensen;
import dk.langli.jensen.JensenBuilder;
import dk.langli.jensen.JsonRpcException;
import dk.langli.jensen.JsonRpcResponse;
import dk.langli.jensen.MethodNotFoundException;
import dk.langli.jensen.ReturnValueHandler;

public class JensenTest {
	private Logger log = LoggerFactory.getLogger(JensenTest.class);

	public void voidCall(Object1 object1, int år) {
		log.info(object1.getObject2().getSvend() + " er " + år + " år gammel");
	}

	public Object2 getObject2(Object1 object1, int år) {
		return object1.getObject2();
	}

	public void notification(Object1 object1, int år) {
		log.trace("Testing notify");
	}

	@Test
	public void testNotification() throws JsonParseException, JsonMappingException, IOException {
		String jsonRequest = getResource("notification.json");
		String responseStr = newJensenBuilder().build().invoke(jsonRequest);
		Assert.assertNull(responseStr);
	}

	@Test
	public void testVoidCall() {
		String jsonRequest = getResource("voidCall.json");
		String response = newJensenBuilder().build().invoke(jsonRequest);
		String expected = getResource("voidCall-response.json");
		Assert.assertEquals(expected.trim(), response.trim());
	}

	@Test
	public void testGetObject2() {
		String jsonRequest = getResource("getObject2.json");
		String response = newJensenBuilder().build().invoke(jsonRequest);
		String expected = getResource("getObject2-response.json");
		Assert.assertEquals(expected.trim(), response.trim());
	}

	@Test
	public void testReturnValueHandler() {
		String jsonRequest = getResource("getObject2.json");
		ReturnValueHandler handler = new ReturnValueHandler() {
			public Object onReturnValue(Object returnValue) throws JsonRpcException {
				return "Return value changed to a string";
			}
		};
		Jensen jensen = newJensenBuilder().build();
		jensen.setReturnValueHandler(handler);
		String response = jensen.invoke(jsonRequest);
		String expected = getResource("returnValueHandler-response.json");
		Assert.assertEquals(expected.trim(), response.trim());
	}

	@Test
	public void testPackagesAreDenied() {
		Jensen jensen = newJensenBuilder().withSecurityFilter(new DefaultSecurityFilter(MatchType.STARTS_WITH, Arrays.asList("dk.langli.jensen.test"))).build();
		String jsonRequest = getResource("javalang.json");
		String response = jensen.invoke(jsonRequest);
		Assert.assertTrue("Expected SecurityException", response.contains("java.lang.SecurityException"));
	}

	private JensenBuilder newJensenBuilder() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		return new JensenBuilder().withObjectMapper(mapper).withPrettyPrinter(new DefaultPrettyPrinter());
	}

	@Test
	public void testJodaModule() {
		JensenBuilder builder = newJensenBuilder();
		builder = builder.withObjectMapper(new ObjectMapper());
		builder.getObjectMapper().registerModule(new JodaModule());
		builder.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		Jensen jensen = builder.build();
		String jsonRequest = getResource("testjoda.json");
		String response = jensen.invoke(jsonRequest);
		// Jackson will write date/time in UTC timezone
		Assert.assertTrue("Expected ISO8601 date string in response", response.contains("2015-03-26T14:29:00.000Z"));
	}

	private String getResource(String filename) {
		String json = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename)));
		String line = null;
		final String LF = System.getProperty("line.separator");
		try {
			while((line = br.readLine()) != null) {
				json += line + LF;
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMethodIncompatibility() throws JsonParseException, JsonMappingException, IOException {
		String jsonRequest = getResource("getObject3.json");
		String responseStr = newJensenBuilder().build().invoke(jsonRequest);
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

}
