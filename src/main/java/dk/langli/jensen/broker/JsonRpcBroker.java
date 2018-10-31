package dk.langli.jensen.broker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import dk.langli.jensen.JsonRpcResponse;
import dk.langli.jensen.Request;
import ru.vyarus.java.generics.resolver.GenericsResolver;

public class JsonRpcBroker {
	public static final String JSONRPC = "2.0";
	private final Logger log = LoggerFactory.getLogger(JsonRpcBroker.class);
	private final ObjectMapper mapper;
	private final ReturnValueHandler returnValueHandler;
	private final ResponseHandler responseHandler;
	private final InvocationIntercepter invocationIntercepter;
	private final MethodLocator methodLocator;
	private final InstanceLocator instanceLocator;
	private final PrettyPrinter prettyPrinter;
	private final SecurityFilter securityFilter;
	private final ExceptionUnwrapFilter exceptionUnwrapFilter;
	private final ExceptionHandler exceptionHandler;

	public static JsonRpcBrokerBuilder builder() {
	    return new JsonRpcBrokerBuilder();
	}

	public JsonRpcBroker(JsonRpcBrokerBuilder builder) {
		mapper = builder.getObjectMapper() != null ? builder.getObjectMapper() : new ObjectMapper();
		returnValueHandler = builder.getReturnValueHandler();
		responseHandler = builder.getResponseHandler();
		invocationIntercepter = builder.getInvocationIntercepter();
		instanceLocator = builder.getInstanceLocator() != null ? builder.getInstanceLocator() : new DefaultInstanceLocator();
		prettyPrinter = builder.getPrettyPrinter();
		securityFilter = builder.getSecurityFilter();
		methodLocator = builder.getMethodLocator() != null ? builder.getMethodLocator() : new DefaultMethodLocator(mapper);
		exceptionUnwrapFilter = builder.getExceptionUnwrapFilter() != null ? builder.getExceptionUnwrapFilter() : e -> true;
		exceptionHandler = builder.getExceptionHandler() != null ? builder.getExceptionHandler() : e -> e;
	}

	public String invoke(String jsonRequest) {
		String responseJson = null;
		JsonRpcResponse response = null;
		Request request = null;
		Object id = null;
		try {
			request = mapper.readValue(jsonRequest, Request.class);
			id = request != null ? request.getId() : null;
			if(request != null) {
				if(request.getJsonrpc().equals(JSONRPC)) {
					if(securityFilter != null && !securityFilter.isAllowed(request)) {
						String message = String.format("Invocation of %s not allowed", request.getMethod());
						throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new SecurityException(message), request, exceptionUnwrapFilter));
					}
					else {
						MethodCall methodCall = methodLocator.getInvocation(request);
						Object result = invoke(methodCall, request);
						if(returnValueHandler != null) {
							result = returnValueHandler.onReturnValue(result);
						}
						response = new JsonRpcResponse(id, result, null);
					}
				}
				else {
					throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("JSON-RPC " + request.getJsonrpc() + " is unsupported.  Use " + JSONRPC + " instead"), request, exceptionUnwrapFilter));
				}
			}
			else {
				throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("Request is missing"), request, exceptionUnwrapFilter));
			}
		}
		catch(JsonRpcException e) {
			response = new JsonRpcResponse(id, null, e.getError());
		}
		catch(MethodNotFoundException e) {
//			Map<String, Object> incompatible = null;
//			if(e.getIncompatibleMethods() != null && e.getIncompatibleMethods().size() > 0) {
//				incompatible = new HashMap<>();
//				incompatible.put("incompatible", e.getIncompatibleMethods());
//			}
			response = new JsonRpcResponse(id, null, JsonRpcError.METHOD_NOT_FOUND.toError(e, Collections.emptyMap(), request, exceptionUnwrapFilter));
		}
		catch(ClassNotFoundException e) {
			response = new JsonRpcResponse(id, null, JsonRpcError.METHOD_NOT_FOUND.toError(e, request, exceptionUnwrapFilter));
		}
		catch(JsonMappingException | JsonParseException e) {
			response = new JsonRpcResponse(id, null, JsonRpcError.PARSE_ERROR.toError(e, request, exceptionUnwrapFilter));
		}
		catch(IOException e) {
			response = new JsonRpcResponse(id, null, JsonRpcError.SERVER_ERROR.toError(e, request, exceptionUnwrapFilter));
		}
		catch(Exception e) {
			response = new JsonRpcResponse(id, null, JsonRpcError.INTERNAL_ERROR.toError(e, request, exceptionUnwrapFilter));
		}
		try {
			if(id != null || request == null) {
				try {
					if(responseHandler != null) {
						response = responseHandler.onResponse(response);
					}
					responseJson = serialize(response);
				}
				catch(JsonRpcException e) {
					responseJson = serialize(new JsonRpcResponse(id, null, JsonRpcError.SERVER_ERROR.toError(e, request, exceptionUnwrapFilter)));
				}
				if(responseJson == null) {
					throw new NullPointerException("Response is null");
				}
			}
		}
		catch(Exception e) {
			Error error = JsonRpcError.SERVER_ERROR.toError(e, request, x -> false);
			try {
				responseJson = serialize(new JsonRpcResponse(id, null, error));
			}
			catch(JsonProcessingException e1) {
				throw new RuntimeException("Failed to serialize response from catched exception", e);
			}
		}
		return responseJson;
	}

	private String serialize(JsonRpcResponse response) throws JsonProcessingException {
		ObjectWriter writer = mapper.writer();
		if(prettyPrinter != null) {
			writer = writer.with(prettyPrinter);
		}
		return writer.writeValueAsString(response);
	}

	private Object invoke(MethodCall methodCall, Request request) throws JsonRpcException {
		Object result = null;
		Method method = methodCall.getMethod();
		String methodSignature = getMethodSignature(methodCall);
		try {
			log.trace("Call " + methodSignature);
			Object instance = null;
			if(!Modifier.isStatic(method.getModifiers())) {
				instance = getInstance(methodCall.getSubjectClass(), request);
			}
			if(invocationIntercepter != null) {
				invocationIntercepter.onBeforeInvocation(method, instance, methodCall.getParams());
			}
			result = method.invoke(instance, methodCall.getParams().toArray());
		}
		catch(IllegalArgumentException e) {
			String message = "Method call " + methodSignature + " cannot accept the parameters given (" + methodCall.getParams().size() + (methodCall.getParams().size() == method.getParameterTypes().length ? " = " : " != ") + method.getParameterTypes().length + ")";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message), request, exceptionUnwrapFilter));
		}
		catch(IllegalAccessException e) {
			String message = "Method call " + methodSignature + " cannot be accessed";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message), request, exceptionUnwrapFilter));
		}
		catch(InvocationTargetException e) {
			String message = "Method call " + methodSignature + " threw an Exception";
			log.warn(message, e);
			Throwable e1 = exceptionHandler.handle(e.getTargetException());
			throw new JsonRpcException(JsonRpcError.SERVER_ERROR.toError(e1, request, exceptionUnwrapFilter));
		}
		return result;
	}

	protected static String getMethodSignature(MethodCall methodCall) {
		Method method = methodCall.getMethod();
		List<Type> parameterTypes = resolveParameterTypes(methodCall.getSubjectClass(), method);
		return method.getName() + "(" + stringify(parameterTypes) + ")";
	}
	
	protected static String stringify(List<? extends Type> parameterTypes) {
		return parameterTypes.stream()
				.map(JsonRpcBroker::simplify)
				.collect(Collectors.joining(", "));
	}
	
	protected static List<Type> resolveParameterTypes(Class<?> classContext, Method method) {
		List<Type> parameterTypes = null;
		try {
			parameterTypes = GenericsResolver.resolve(classContext).method(method).resolveParametersTypes();
		}
		catch(Exception e) {
			parameterTypes = Collections.emptyList();
		}
		return parameterTypes;
	}

	private static String simplify(Type type) {
		String name = type.getTypeName();
		return name.replaceFirst("(?:[^.<]+\\.)*", "").replaceAll("(<|, ?)(?:[^\\.]+\\.)*", "$1");
	}

	private Object getInstance(Class<?> clazz, Request request) throws JsonRpcException {
		Object instance = null;
		try {
			instance = instanceLocator.getInstance(clazz);
		}
		catch(InstantiationException e) {
			String message = "Class " + clazz.getSimpleName() + " cannot be instantiated with a no-args constructor";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message), request, exceptionUnwrapFilter));
		}
		catch(IllegalAccessException e) {
			String message = "Class " + clazz.getSimpleName() + " must not be instantiated with a no-args constructor";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message), request, exceptionUnwrapFilter));
		}
		return instance;
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

	public InstanceLocator getInstanceFinder() {
		return instanceLocator;
	}
}
