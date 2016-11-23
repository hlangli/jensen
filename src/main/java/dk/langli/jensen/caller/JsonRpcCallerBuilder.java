package dk.langli.jensen.caller;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRpcCallerBuilder {
	private ObjectMapper objectMapper = null;
	private PrettyPrinter prettyPrinter = null;
	private Transport transport = null;

	public JsonRpcCallerBuilder() {
	}
	
	public JsonRpcCaller build() {
		return new JsonRpcCaller(this);
	}

	public JsonRpcCallerBuilder withTransport(Transport transport) {
		this.transport = transport;
		return this;
	}

	public JsonRpcCallerBuilder withObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public JsonRpcCallerBuilder withPrettyPrinter(PrettyPrinter prettyPrinter) {
		this.prettyPrinter = prettyPrinter;
		return this;
	}

	public PrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

    public Transport getTransport() {
        return transport;
    }
}
