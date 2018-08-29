package dk.langli.jensen.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dk.langli.jensen.Request;
import dk.langli.jensen.broker.DefaultPrettyPrinter;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.JsonRpcBrokerBuilder;

public class ExceptionTest {
	private static final String EXCEPTION_PARAMETER = "%&/=/()=¤#HJFDSJHKLFSJDKL";
	
	@Test
	public void testException() {
		String responseStr = newJensenBuilder()
				.withPrettyPrinter(new DefaultPrettyPrinter())
				.build()
				.invoke(s(req(1, "throwException", null)));
		assertNotEquals(-1, responseStr.indexOf(EXCEPTION_PARAMETER));
	}

	private JsonRpcBrokerBuilder newJensenBuilder() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		return JsonRpcBroker.builder().withObjectMapper(mapper);
	}

	public void throwException() throws AugmentedException {
		throw new AugmentedException(EXCEPTION_PARAMETER, "Exception happened");
	}

	private static Request req(Object id, String method, List<? extends Object> params) {
		return req(id, ExceptionTest.class.getName(), method, params);
	}

	private static Request req(Object id, String className, String method, List<? extends Object> params) {
		return new Request(id, String.format("%s.%s", className, method), params);
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
}
