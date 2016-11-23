package dk.langli.jensen.broker;

import java.lang.reflect.Method;
import java.util.List;

public interface InvocationIntercepter {
	public void onBeforeInvocation(Method method, Object instance, List<? extends Object> params);
}
