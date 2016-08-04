package dk.langli.jensen;

import java.lang.reflect.Method;
import java.util.List;

public interface InvocationIntercepter {
	public void onBeforeInvocation(Method method, Object instance, List<Object> params);
}
