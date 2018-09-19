package dk.langli.jensen.broker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.langli.jensen.Request;
import ru.vyarus.java.generics.resolver.GenericsResolver;

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
		List<Method> methods = Arrays.asList(clazz.getMethods()).stream()
   			.filter(m -> m.getAnnotation(JsonRpcIgnore.class) == null)
   			.collect(Collectors.toList());
		int methodIndex = 0;
		Map<String, IncompatibleParameter> incompatibleMethods = new HashMap<>();
		while(methodCall == null && methodIndex < methods.size()) {
			Method method = methods.get(methodIndex++);
			String signature = method.getName() + "(" + toString(Arrays.asList(method.getGenericParameterTypes())) + ")";
			if(method.getName().equals(methodName)) {
				if(Modifier.isPublic(method.getModifiers())) {
					List<Class<?>> parameterTypes = getParameterTypes(clazz, method);
					log.trace("Check method parameter compatibility: " + method.getName() + "(" + toString(parameterTypes) + ")");
					try {
						List<? extends Object> params = deserializeParameterList(requestParams, parameterTypes);
						if((requestParams == null && parameterTypes.size() == 0) || parameterTypes.size() == requestParams.size()) {
							log.trace(signature + " is compatible with the parameter list");
							methodCall = new MethodCall(clazz, method, params);
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

	private List<Class<?>> getParameterTypes(Class<?> clazz, Method method) {
		return GenericsResolver
				.resolve(clazz)
				.method(method)
				.resolveParameters();
	}

	private String toString(List<? extends Type> parameterTypes) {
		String parmTypes = "";
		for(int i = 0; parameterTypes != null && i < parameterTypes.size(); i++) {
			Type type = parameterTypes.get(i);
			parmTypes += (i != 0 ? ", " : "") + (type instanceof Class<?> ? ((Class<?>) type).getSimpleName() : type.getTypeName());
		}
		return parmTypes;
	}

	private List<Object> deserializeParameterList(List<? extends Object> params, List<Class<?>> parameterTypes) throws ParameterTypeException {
		List<Object> deserializedparams = new ArrayList<Object>();
		if(params != null && params.size() == parameterTypes.size()) {
			for(int i = 0; i < parameterTypes.size(); i++) {
				try {
					Object o = mapper.convertValue(params.get(i), parameterTypes.get(i));
					deserializedparams.add(o);
				}
				catch(IllegalArgumentException e) {
					String message = String.format("Parameter[%s] is not a %s", i, parameterTypes.get(i).getSimpleName());
					throw new ParameterTypeException(message, parameterTypes.get(i), i);
				}
			}
		}
		return deserializedparams;
	}
}
