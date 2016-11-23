package dk.langli.jensen;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.langli.jensen.broker.InstanceLocator;
import dk.langli.jensen.broker.InvocationIntercepter;
import dk.langli.jensen.broker.JsonRpcBroker;
import dk.langli.jensen.broker.MethodLocator;
import dk.langli.jensen.broker.ResponseHandler;
import dk.langli.jensen.broker.ReturnValueHandler;
import dk.langli.jensen.broker.SecurityFilter;

/**
 * Build a Jensen feature with specific features
 * 
 * @author Rune Molin, rmo@nineconsult.dk
 */
public class JsonBrokerBuilder {
	private ObjectMapper objectMapper = null;
	private SecurityFilter securityFilter = null;
	private PrettyPrinter prettyPrinter = null;
	private ReturnValueHandler returnValueHandler = null;
	private ResponseHandler responseHandler = null;
	private InvocationIntercepter invocationIntercepter = null;
	private InstanceLocator instanceLocator = null;
	private MethodLocator methodLocator = null;

	public JsonBrokerBuilder() {
	}
	
	public JsonRpcBroker build() {
		return new JsonRpcBroker(this);
	}

	public JsonBrokerBuilder withMethodLocator(MethodLocator methodLocator) {
		this.methodLocator = methodLocator;
		return this;
	}

	public JsonBrokerBuilder withInstanceLocator(InstanceLocator instanceLocator) {
		this.instanceLocator = instanceLocator;
		return this;
	}

	public JsonBrokerBuilder withInvocationIntercepter(InvocationIntercepter invocationIntercepter) {
		this.invocationIntercepter = invocationIntercepter;
		return this;
	}

	public JsonBrokerBuilder withResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
		return this;
	}

	public JsonBrokerBuilder withReturnValueHandler(ReturnValueHandler returnValueHandler) {
		this.returnValueHandler = returnValueHandler;
		return this;
	}

	public JsonBrokerBuilder withObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public JsonBrokerBuilder withSecurityFilter(SecurityFilter securityFilter) {
		this.securityFilter = securityFilter;
		return this;
	}

	public JsonBrokerBuilder withPrettyPrinter(PrettyPrinter prettyPrinter) {
		this.prettyPrinter = prettyPrinter;
		return this;
	}

	public PrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public SecurityFilter getSecurityFilter() {
		return securityFilter;
	}

	public ReturnValueHandler getReturnValueHandler() {
		return returnValueHandler;
	}

	public ResponseHandler getResponseHandler() {
		return responseHandler;
	}

	public InvocationIntercepter getInvocationIntercepter() {
		return invocationIntercepter;
	}

	public InstanceLocator getInstanceLocator() {
		return instanceLocator;
	}

	public MethodLocator getMethodLocator() {
		return methodLocator;
	}
}
