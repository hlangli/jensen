package dk.langli.jensen;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Build a Jensen feature with specific features
 * 
 * @author Rune Molin, rmo@nineconsult.dk
 */
public class JensenBuilder {
	private ObjectMapper objectMapper = null;
	private SecurityFilter securityFilter = null;
	private PrettyPrinter prettyPrinter = null;
	private ReturnValueHandler returnValueHandler = null;
	private ResponseHandler responseHandler = null;
	private InvocationIntercepter invocationIntercepter = null;
	private InstanceLocator instanceLocator = null;
	private MethodLocator methodLocator = null;

	public JensenBuilder() {
	}
	
	public Jensen build() {
		return new Jensen(this);
	}

	public JensenBuilder withMethodLocator(MethodLocator methodLocator) {
		this.methodLocator = methodLocator;
		return this;
	}

	public JensenBuilder withInstanceLocator(InstanceLocator instanceLocator) {
		this.instanceLocator = instanceLocator;
		return this;
	}

	public JensenBuilder withInvocationIntercepter(InvocationIntercepter invocationIntercepter) {
		this.invocationIntercepter = invocationIntercepter;
		return this;
	}

	public JensenBuilder withResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
		return this;
	}

	public JensenBuilder withObjectMapper(ReturnValueHandler returnValueHandler) {
		this.returnValueHandler = returnValueHandler;
		return this;
	}

	public JensenBuilder withObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public JensenBuilder withSecurityFilter(SecurityFilter securityFilter) {
		this.securityFilter = securityFilter;
		return this;
	}

	public JensenBuilder withPrettyPrinter(PrettyPrinter prettyPrinter) {
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
