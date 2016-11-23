package dk.langli.jensen.broker;

import java.lang.reflect.Method;
import java.util.List;

class MethodCall {
	private Method method = null;
	private List<? extends Object> params = null;
	
	public MethodCall(Method method, List<? extends Object> params) {
		this.method = method;
		this.params = params;
	}

	public Method getMethod() {
		return method;
	}

	public List<? extends Object> getParams() {
		return params;
	}

}
