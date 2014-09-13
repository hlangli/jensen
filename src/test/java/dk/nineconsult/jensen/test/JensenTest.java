package dk.nineconsult.jensen.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.nineconsult.jensen.Jensen;
import dk.nineconsult.jensen.JsonRpcException;
import dk.nineconsult.jensen.ReturnValueHandler;

public class JensenTest {
	private Logger log = LoggerFactory.getLogger(JensenTest.class);
	
	public void voidCall(Object1 object1, int år) {
		log.info(object1.getObject2().getSvend()+" er "+år+" år gammel");
	}
	
	public Object2 getObject2(Object1 object1, int år) {
		return object1.getObject2();
	}
	
	@Test
	public void testNotification() {
		String jsonRequest = getResource("notification.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		assert response == null;
	}

	@Test
	public void testVoidCall() {
		String jsonRequest = getResource("voidCall.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("voidCall-response.json");
		assert response.trim().equals(expected.trim());
	}

	@Test
	public void testGetObject2() {
		String jsonRequest = getResource("getObject2.json");
		log.trace(jsonRequest);
		String response = new Jensen().invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("getObject2-response.json");
		assert response.trim().equals(expected.trim());
	}

	@Test
	public void testReturnValueHandler() {
		String jsonRequest = getResource("getObject2.json");
		log.trace(jsonRequest);
		ReturnValueHandler handler = new ReturnValueHandler() {
			public Object onReturnValue(Object returnValue) throws JsonRpcException {
				return null;
			}
		};
		String response = new Jensen().invoke(jsonRequest);
		log.trace(response);
		String expected = getResource("getObject2-response.json");
		assert response.trim().equals(expected.trim());
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
}
