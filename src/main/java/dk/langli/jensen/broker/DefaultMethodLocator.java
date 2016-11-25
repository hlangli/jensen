package dk.langli.jensen.broker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.langli.jensen.Request;

public class DefaultMethodLocator implements MethodLocator {
	private final Logger log = LoggerFactory.getLogger(DefaultMethodLocator.class);
	private final ObjectMapper mapper;

	public DefaultMethodLocator(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static String getMethodName(Request request) {
		return request.getMethod().substring(request.getMethod().lastIndexOf('.') + 1);
	}

	public static String getClassName(Request request) {
		return request.getMethod().substring(0, request.getMethod().lastIndexOf('.'));
	}

	@Override
	public MethodCall getInvocation(Request request) throws ClassNotFoundException, MethodNotFoundException {
		MethodCall methodCall = null;
		String methodName = getMethodName(request);
		String className = getClassName(request);
		List<? extends Object> requestParams = request.getParams();
		Class<?> clazz = Class.forName(className);
		methodCall = getMethodCall(clazz, methodName, requestParams);
		if(methodCall == null) {
			throw new MethodNotFoundException(String.format("Method %s in class %s not found", methodName, className), null);
		}
		return methodCall;
	}

	private MethodCall getMethodCall(Class<?> clazz, String methodName, List<? extends Object> requestParams) throws MethodNotFoundException {
		MethodCall methodCall = null;
		Method[] methods = clazz.getMethods();
		int methodIndex = 0;
		Map<String, IncompatibleParameter> incompatibleMethods = new HashMap<>();
		while(methodCall == null && methodIndex < methods.length) {
			Method method = methods[methodIndex++];
			String signature = method.getName() + "(" + toString(method.getParameterTypes()) + ")";
			if(method.getName().equals(methodName)) {
				if(Modifier.isPublic(method.getModifiers())) {
					log.trace("Check method parameter compatibility: " + method.getName() + "(" + toString(method.getParameterTypes()) + ")");
					try {
						List<? extends Object> params = deserializeParameterList(requestParams, method.getParameterTypes());
						if(requestParams == null && params.size() == 0 || params.size() == requestParams.size()) {
							log.trace(signature + " is compatible with the parameter list");
							methodCall = new MethodCall(method, params);
						}
					}
					catch(ParameterTypeException e) {
						incompatibleMethods.put(signature, e.getIncompatibleParameter());
					}
				}
			}
		}
		if(methodCall == null) {
			String message = String.format("No method %s in class %s can take the given parameters", methodName, clazz.getSimpleName());
			throw new MethodNotFoundException(message, incompatibleMethods);
		}
		return methodCall;
	}

	private String toString(Class<?>[] parameterTypes) {
		String parmTypes = "";
		for(int i = 0; parameterTypes != null && i < parameterTypes.length; i++) {
			Class<?> type = parameterTypes[i];
			parmTypes += (i != 0 ? ", " : "") + type.getSimpleName();
		}
		return parmTypes;
	}

	private List<Object> deserializeParameterList(List<? extends Object> params, Class<?>[] parameterTypes) throws ParameterTypeException {
		List<Object> deserializedparams = new ArrayList<Object>();
		if(params != null && params.size() == parameterTypes.length) {
			for(int i = 0; i < parameterTypes.length; i++) {
				try {
					Object o = mapper.convertValue(params.get(i), parameterTypes[i]);
					deserializedparams.add(o);
				}
				catch(IllegalArgumentException e) {
					String message = String.format("Parameter[%s] is not a %s", i, parameterTypes[i].getSimpleName());
					throw new ParameterTypeException(message, parameterTypes[i], i);
				}
			}
		}
		return deserializedparams;
	}
}
