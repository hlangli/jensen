package dk.langli.jensen.broker;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class JsonInputStream implements JsonSerializable {
	private InputStream in = null;
	
	public JsonInputStream(InputStream in) {
		this.in = in;
	}

	public void serialize(JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(in);
		while(parser.nextToken() != null) {
			generator.copyCurrentStructure(parser);
		}
	}

	public void serializeWithType(JsonGenerator generator, SerializerProvider provider, TypeSerializer typeSerializer) throws IOException, JsonProcessingException {
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(in);
		while(parser.nextToken() != null) {
			generator.copyCurrentStructure(parser);
		}
	}
}
