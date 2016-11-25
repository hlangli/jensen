package dk.langli.jensen.caller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import dk.langli.jensen.Request;

@SuppressWarnings("serial")
public class JsonRpcExceptionDeserializer extends StdDeserializer<JsonRpcException> {
	private final ObjectMapper objectMapper;
	
	protected JsonRpcExceptionDeserializer(ObjectMapper objectMapper) {
		super(JsonRpcException.class);
		this.objectMapper = objectMapper;
	}

	@Override
	public JsonRpcException deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		Throwable cause = objectMapper.convertValue(node.get("cause"), JsonRpcException.class);
		JsonRpcException exception = new JsonRpcException(node.get("message").asText(), cause);
		exception.setException(node.get("exception").asText());
		JsonNode request = node.get("request");
		if(request != null) {
			exception.setRequest(objectMapper.convertValue(request, Request.class));
		}
		String[] stackTrace = objectMapper.convertValue(node.get("stackTrace"), String[].class);
		if(stackTrace != null) {
			exception.setStackTrace(stackTrace);
		}
		return exception;

	}
}
