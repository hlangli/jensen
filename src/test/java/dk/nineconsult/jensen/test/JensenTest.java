package dk.nineconsult.jensen.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.nineconsult.jensen.JensenBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.nineconsult.jensen.Jensen;
import dk.nineconsult.jensen.JsonRpcException;
import dk.nineconsult.jensen.ReturnValueHandler;

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
	public void testNotification() {
		log.trace("testNotification()");
		String jsonRequest = getResource("notification.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		assert response == null;
	}

	@Test
	public void testVoidCall() {
		log.trace("testVoidCall()");
		String jsonRequest = getResource("voidCall.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("voidCall-response.json");
		assert response.trim().equals(expected.trim());
	}

	@Test
	public void testGetObject2() {
		log.trace("testGetObject2()");
		String jsonRequest = getResource("getObject2.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("getObject2-response.json");
		assert response.trim().equals(expected.trim());
	}

	@Test
	public void testReturnValueHandler() {
		log.trace("testReturnValueHandler()");
		String jsonRequest = getResource("getObject2.json");
		log.trace(jsonRequest);
		ReturnValueHandler handler = new ReturnValueHandler() {
			public Object onReturnValue(Object returnValue) throws JsonRpcException {
				return "Return value changed to a string";
			}
		};
		Jensen jensen = new Jensen();
		jensen.setReturnValueHandler(handler);
		String response = jensen.invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("returnValueHandler-response.json");
		;
		assert response.trim().equals(expected.trim());
	}

	@Test
	public void testPackagesAreDenied() {
		Jensen jensen = new JensenBuilder()
			.allowPackage("dk.nineconsult.jensen.test")
			.build();

		String jsonRequest = getResource("javalang.json");

		String response = jensen.invoke(jsonRequest);

		Assert.assertTrue("Expected IllegalArgumentException", response.contains("java.lang.IllegalArgumentException"));
	}

	@Test
	public void testJodaModule() {
		Jensen jensen = new JensenBuilder()
			.registerJacksonModule(JodaModule.class)
			.disableSerializationFeature(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.build();

		String jsonRequest = getResource("testjoda.json");

		String response = jensen.invoke(jsonRequest);

		log.trace(response);

		// Jackson will write date/time in UTC timezone
		Assert.assertTrue("Expected ISO8601 date string in response", response.contains("2015-03-26T14:29:00.000Z"));
	}


	private String getResource(String filename) {
		String json = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename)));
		String line = null;
		final String LF = System.getProperty("line.separator");
		try {
			while ((line = br.readLine()) != null) {
				json += line + LF;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
}
