package dk.langli.jensen.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dk.langli.jensen.Request;
import dk.langli.jensen.broker.DefaultPrettyPrinter;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.JsonRpcBrokerBuilder;

public class GenericsTest {
	private static Boolean saveMethodCalled = false;
	
	@Test
	public void testGenerics() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonRpcBroker broker = newJensenBuilder().withPrettyPrinter(new DefaultPrettyPrinter()).build();
		CreditCard creditCard = new CreditCard();
		creditCard.setBrand("visa");
		creditCard.setCardHolderName("Kaj Svendsen");
		creditCard.setCreditCardNumber("42442424224242");
		creditCard.setCvc(292);
//		creditCard.setExpiration(YearMonth.parse("2021-10"));
		creditCard.setId("KajsKortId");
		String responseStr = broker.invoke(s(req(1, "save", l("KajsId", l(creditCard)))));
		System.out.println(responseStr);
		assertTrue(saveMethodCalled);
	}

	private JsonRpcBrokerBuilder newJensenBuilder() {
		ObjectMapper mapper = new ObjectMapper()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.findAndRegisterModules()
				.registerModule(new JavaTimeModule());
		return JsonRpcBroker.builder().withObjectMapper(mapper);
	}

	private static Request req(Object id, String method, List<? extends Object> params) {
		return req(id, GenericsTest.class.getName(), method, params);
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

	public void save(String customerId, List<CreditCard> creditCards) {
		assertEquals(CreditCard.class, creditCards.get(0).getClass());
		saveMethodCalled = true;
	}
}
