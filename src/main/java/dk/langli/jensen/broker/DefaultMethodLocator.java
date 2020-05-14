package dk.langli.jensen.broker;

import static dk.langli.jensen.broker.JsonRpcBroker.*;

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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import dk.langli.jensen.Request;

public class DefaultMethodLocator implements MethodLocator {
	private final Logger log = LoggerFactory.getLogger(DefaultMethodLocator.class);
	private final ExceptionUnwrapFilter exceptionUnwrapFilter;
	private final ObjectMapper mapper;
	private final TypeFactory tf;

	public DefaultMethodLocator(ObjectMapper mapper, ExceptionUnwrapFilter exceptionUnwrapFilter) {
		this.mapper = mapper;
		this.exceptionUnwrapFilter = exceptionUnwrapFilter;
		this.tf = TypeFactory.defaultInstance();
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
			List<Type> parameterTypes = resolveParameterTypes(clazz, method);
			String signature = method.getName() + "(" + stringify(parameterTypes) + ")";
			if(method.getName().equals(methodName)) {
				if(Modifier.isPublic(method.getModifiers())) {
					log.trace("Check method parameter compatibility: " + method.getName() + "(" + stringify(parameterTypes) + ")");
					try {
						List<? extends Object> params = deserializeParameterList(clazz, requestParams, parameterTypes);
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
	
	private List<Object> deserializeParameterList(Class<?> contextClass, List<? extends Object> params, List<Type> parameterTypes) throws ParameterTypeException {
		List<Object> deserializedparams = new ArrayList<Object>();
		if(params != null && params.size() == parameterTypes.size()) {
			for(int i = 0; i < parameterTypes.size(); i++) {
				Object param = params.get(i);
				Type parameterType = parameterTypes.get(i);
				try {
					JavaType genericType = tf.constructType(parameterType, contextClass);
					Object o = genericType.isPrimitive() || param != null ? o = mapper.convertValue(param, genericType) : null;
					deserializedparams.add(o);
				}
				catch(IllegalArgumentException e) {
					String message = String.format("Parameter[%s] is not a %s", i, parameterType.getTypeName());
					throw new ParameterTypeException(message, parameterType, i, new JsonCause(e, exceptionUnwrapFilter));
				}
			}
		}
		return deserializedparams;
	}
}
