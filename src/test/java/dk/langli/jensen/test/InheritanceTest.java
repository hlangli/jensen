package dk.langli.jensen.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dk.langli.jensen.Request;
import dk.langli.jensen.broker.DefaultPrettyPrinter;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.JsonRpcBrokerBuilder;
import dk.langli.jensen.broker.JsonRpcIgnore;

public class InheritanceTest {
	@Test
	public void testInheritedMethod() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonRpcBroker broker = newJensenBuilder().withPrettyPrinter(new DefaultPrettyPrinter()).build();
		String responseStr = broker.invoke(s(req(1, "testClassMethod", null)));
		assertEquals("testClassMethod", mapper.readValue(responseStr, Map.class).get("result"));
		responseStr = broker.invoke(s(req(1, "testSuperClassMethod", null)));
		assertEquals("testSuperClassMethod", mapper.readValue(responseStr, Map.class).get("result"));
		responseStr = broker.invoke(s(req(1, "testInterfaceMethod", l(new TestPayload("HelloWorld"), TestPayload.class.getName()))));
		assertEquals("testInterfaceMethod", mapper.readValue(responseStr, Map.class).get("result"));
		responseStr = broker.invoke(s(req(1, "testIgnoredMethod", null)));
		assertNull(mapper.readValue(responseStr, Map.class).get("result"));
		responseStr = broker.invoke(s(req(1, TestIntermediateClass.class.getName(), "testIgnoredMethod", null)));
		assertNotNull(mapper.readValue(responseStr, Map.class).get("result"));
	}

	private JsonRpcBrokerBuilder newJensenBuilder() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		return JsonRpcBroker.builder().withObjectMapper(mapper);
	}

	private static Request req(Object id, String method, List<? extends Object> params) {
		return req(id, TestClass.class.getName(), method, params);
	}

	private static Request req(Object id, String className, String method, List<? extends Object> params) {
		return new Request(id, String.format("%s.%s", className, method), params);
	}

	private static List<? extends Object> l(Object... params) {
		return Arrays.asList(params);
	}

	private static String s(Object m) {
		return s(m, new ObjectMapper());
	}

	private static String s(Object m, ObjectMapper objectMapper) {
		try {
			return objectMapper.writeValueAsString(m);
		}
		catch(JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class TestClass extends TestIntermediateClass {
		public String testClassMethod() {
			return new Throwable().getStackTrace()[0].getMethodName();
		}

		@JsonRpcIgnore
		public String testIgnoredMethod() {
			return new Throwable().getStackTrace()[0].getMethodName();
		}
	}

	public static class TestIntermediateClass extends TestSuperClass {
		public String testClassMethod() {
			return new Throwable().getStackTrace()[0].getMethodName();
		}

		public String testIgnoredMethod() {
			return new Throwable().getStackTrace()[0].getMethodName();
		}
	}

	public static abstract class TestSuperClass implements TestInterface<TestPayload> {
		public String testSuperClassMethod() {
			return new Throwable().getStackTrace()[0].getMethodName();
		}
	}

	public static interface TestInterface<P> {
		public default String testInterfaceMethod(P payload, String payloadType) {
			assertEquals(payloadType, payload.getClass().getName());
			return new Throwable().getStackTrace()[0].getMethodName();
		}
	}

	public static class TestPayload {
		private String content;

		public TestPayload() {
		}

		public TestPayload(String content) {
			this.content = content;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
}
