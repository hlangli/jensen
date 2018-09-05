package dk.langli.jensen.broker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

class JsonCause {
	private String exception = null;
	private String message = null;
	private String[] stackTrace = null;
	private JsonCause cause = null;
	private Map<String, Object> data;

	public JsonCause(Throwable target) {
		this(target, null);
	}

	public JsonCause(Throwable target, Map<String, Object> data) {
		exception = target.getClass().getName();
		message = target.getMessage();
		StackTraceElement[] stackTrace = target.getStackTrace();
		this.stackTrace = stackTrace != null ? new String[stackTrace.length] : null;
		for(int i = 0; stackTrace != null && i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			this.stackTrace[i] = element.toString();
		}
		Throwable cause = target.getCause();

		this.data = Arrays.asList(target.getClass().getDeclaredMethods()).stream()
				.filter(m -> m.getName().startsWith("get") || m.getName().startsWith("is"))
				.filter(m -> !m.getReturnType().equals(Void.TYPE))
				.collect(toNvlMap(m -> getKey(m), m -> getValue(target, m), (a, b) -> b));
		if(data != null) {
			this.data.putAll(data);
		}
		this.cause = cause != null ? new JsonCause(cause) : null;
	}

	private Object getValue(Object o, Method method) {
		Object value = null;
		try {
			value = method.invoke(o);
		}
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			value = String.format("{%s}: %s", e.getClass().getName(), e.getMessage());
		}
		return value;
	}

	private static String getKey(Method method) {
		String methodName = method.getName();
		String key = null;
		if(methodName.startsWith("get")) {
			key = StringUtils.uncapitalize(methodName.substring(3));
		}
		else {
			if(methodName.startsWith("is")) {
				key = StringUtils.uncapitalize(methodName.substring(2));
			}
		}
		return key;
	}

	public String getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	public String[] getStackTrace() {
		return stackTrace;
	}

	public JsonCause getCause() {
		return cause;
	}

	@JsonAnyGetter
	public Map<String, Object> getData() {
		return data;
	}

	private static <T, K, U> Collector<T, ?, Map<K, U>> toNvlMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			Map<K, U> result = new HashMap<>();
			for(T item : list) {
				K key = keyMapper.apply(item);
				U value = valueMapper.apply(item);
				if(result.containsKey(key)) {
					value = mergeFunction.apply(result.get(key), valueMapper.apply(item));
				}
				result.put(key, value);
			}
			return result;
		});
	}
}
