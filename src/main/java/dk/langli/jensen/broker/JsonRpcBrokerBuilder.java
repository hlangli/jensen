package dk.langli.jensen.broker;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Build a Jensen feature with specific features
 * 
 * @author Rune Molin, rmo@nineconsult.dk
 */
public class JsonRpcBrokerBuilder {
	private ObjectMapper objectMapper = null;
	private SecurityFilter securityFilter = null;
	private PrettyPrinter prettyPrinter = null;
	private ReturnValueHandler returnValueHandler = null;
	private ResponseHandler responseHandler = null;
	private InvocationIntercepter invocationIntercepter = null;
	private InstanceLocator instanceLocator = null;
	private MethodLocator methodLocator = null;

	protected JsonRpcBrokerBuilder() {
	}
	
	public JsonRpcBroker build() {
		return new JsonRpcBroker(this);
	}

	public JsonRpcBrokerBuilder withMethodLocator(MethodLocator methodLocator) {
		this.methodLocator = methodLocator;
		return this;
	}

	public JsonRpcBrokerBuilder withInstanceLocator(InstanceLocator instanceLocator) {
		this.instanceLocator = instanceLocator;
		return this;
	}

	public JsonRpcBrokerBuilder withInvocationIntercepter(InvocationIntercepter invocationIntercepter) {
		this.invocationIntercepter = invocationIntercepter;
		return this;
	}

	public JsonRpcBrokerBuilder withResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
		return this;
	}

	public JsonRpcBrokerBuilder withReturnValueHandler(ReturnValueHandler returnValueHandler) {
		this.returnValueHandler = returnValueHandler;
		return this;
	}

	public JsonRpcBrokerBuilder withObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public JsonRpcBrokerBuilder withSecurityFilter(SecurityFilter securityFilter) {
		this.securityFilter = securityFilter;
		return this;
	}

	public JsonRpcBrokerBuilder withPrettyPrinter(PrettyPrinter prettyPrinter) {
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
