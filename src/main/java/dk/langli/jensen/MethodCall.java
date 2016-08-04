package dk.langli.jensen;

import java.lang.reflect.Method;
import java.util.List;

class MethodCall {
	private Method method = null;
	private List<Object> params = null;
	
	public MethodCall(Method method, List<Object> params) {
		this.method = method;
		this.params = params;
	}

	public Method getMethod() {
		return method;
	}

	public List<Object> getParams() {
		return params;
	}

}
