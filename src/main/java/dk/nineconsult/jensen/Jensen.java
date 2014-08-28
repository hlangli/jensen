package dk.nineconsult.jensen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Jensen {
	protected static final String JSONRPC = "2.0";
	private Logger log = LoggerFactory.getLogger(Jensen.class);
	private Gson gson = null;
	private Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
	
	public Jensen(GsonBuilder gsonBuilder) {
		gson = gsonBuilder.create();
	}

	public Jensen() {
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.enableComplexMapKeySerialization();
			gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			gsonBuilder.setPrettyPrinting();
			gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
			gson = gsonBuilder.create();
		}
		catch(Throwable e) {
			log.error("Cannot instantiate gson");
		}
	}

	public String invoke(String jsonRequest) {
		Response response = null;
		Request request = null;
		Integer id = null;
		try {
			request = gson.fromJson(jsonRequest, Request.class);
			id = request != null ? request.getId() : null;
			if(request != null) {
				if(request.getJsonrpc().equals(JSONRPC)) {
					MethodCall methodCall = getMethodCall(request);
					Object result = invoke(methodCall);
					response = new Response(id, result, null);
				}
				else {
					throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("JSON-RPC "+request.getJsonrpc()+" is unsupported.  Use "+JSONRPC+" instead")));
				}
			}
			else {
				throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("Request is missing")));
			}
		}
		catch(JsonRpcException e) {
			response = new Response(id, null, e.getError());
		}
		catch(Throwable e) {
			response = new Response(id, null, JsonRpcError.PARSE_ERROR.toError(e));
		}
		return id != null || request == null ? gson.toJson(response) : null;
	}
	
	private Object invoke(MethodCall methodCall) throws JsonRpcException {
		Object result = null;
		Method method = methodCall.getMethod();
		String methodSignature = getMethodSignature(methodCall);
		try {
			log.trace("Call "+methodSignature);
			result = method.invoke(getInstance(method.getDeclaringClass()), methodCall.getParams().toArray());
		}
		catch(IllegalArgumentException e) {
			String message = "Method call "+methodSignature+" cannot accept the parameters given ("+methodCall.getParams().size()+(methodCall.getParams().size() == method.getParameterTypes().length ? " = " : " != ")+method.getParameterTypes().length+")";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message)));
		}
		catch(IllegalAccessException e) {
			String message = "Method call "+methodSignature+" cannot be accessed";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message)));
		}
		catch(InvocationTargetException e) {
			String message = "Method call "+methodSignature+" threw an Exception";
			log.warn(message, e);
			throw new JsonRpcException(JsonRpcError.SERVER_ERROR.toError(e.getTargetException()));
		}
		return result;
	}
	
	private String getMethodSignature(MethodCall methodCall) {
		Method method = methodCall.getMethod();
		List<Object> params = methodCall.getParams();
		String methodSignature = method.getName()+"(";
		for(int i=0; params != null && i<params.size(); i++) {
			methodSignature += (i != 0 ? ", " : "")+params.get(i).getClass().getSimpleName();
		}
		methodSignature += ")";
		return methodSignature;
	}
	
	private Object getInstance(Class<?> clazz) throws JsonRpcException {
		Object instance = instances.get(clazz);
		if(instance == null) {
			try {
				instance = clazz.newInstance();
				instances.put(clazz, instance);
			}
			catch(InstantiationException e) {
				String message = "Class "+clazz.getSimpleName()+" cannot be instantiated with a no-args constructor";
				log.warn(message, e);
				throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message)));
			}
			catch(IllegalAccessException e) {
				String message = "Class "+clazz.getSimpleName()+" must not be instantiated with a no-args constructor";
				log.warn(message, e);
				throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message)));
			}
		}
		return instance;
	}
	
	private MethodCall getMethodCall(Request request) throws JsonRpcException {
		MethodCall methodCall = null;
		String qualifiedMethodName = request.getMethod();
		String methodName = null;
		String className = null;
		try {
			methodName = qualifiedMethodName.substring(qualifiedMethodName.lastIndexOf('.')+1);
			className = qualifiedMethodName.substring(0, qualifiedMethodName.lastIndexOf('.'));
			List<Object> requestParams = request.getParams();
			Class<?> clazz = Class.forName(className);
			methodCall = getMethodCall(clazz, methodName, requestParams);
		}
		catch(Throwable e) {
			throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(e));
		}
		if(methodCall == null) {
			String message = "No method \""+methodName+"\" in class "+className+" can take the given parameter types";
			log.warn(message);
			throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message)));
		}
		return methodCall;
	}
	
	private MethodCall getMethodCall(Class<?> clazz, String methodName, List<Object> requestParams) {
		MethodCall methodCall = null;
		Method[] methods = clazz.getMethods();
		int methodIndex = 0;
		while(methodCall == null && methodIndex <= methods.length) {
			Method method = methods[methodIndex++];
			if(method.getName().equals(methodName)) {
				log.trace("CHECK "+method.getName()+"("+toString(method.getParameterTypes())+")");
				try {
					List<Object> params = deserializeParameterList(requestParams, method.getParameterTypes());
					methodCall = new MethodCall(method, params);
				}
				catch(Throwable e) {
					//FIXME: Swallow and ignore - Bad coding practice.
				}
			}
		}
		return methodCall;
	}
	
	private String toString(Class<?>[] parameterTypes) {
		String parmTypes = "";
		for(int i=0; parameterTypes != null && i<parameterTypes.length; i++) {
			Class<?> type = parameterTypes[i];
			parmTypes += (i != 0 ? ", " : "")+type.getSimpleName();
		}
		return parmTypes;
	}
	
	private List<Object> deserializeParameterList(List<Object> params, Class<?>[] parameterTypes) {
		List<Object> deserializedparams = new ArrayList<Object>();
		if(params.size() == parameterTypes.length) {
			for(int i=0; i<parameterTypes.length; i++) {
				Object o = gson.fromJson(params.get(i).toString(), parameterTypes[i]);
				deserializedparams.add(o);
			}
		}
		return deserializedparams;
	}
}
