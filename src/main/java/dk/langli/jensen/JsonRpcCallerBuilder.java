package dk.langli.jensen;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.langli.jensen.caller.JsonRpcCaller;
import dk.langli.jensen.caller.Transport;

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
