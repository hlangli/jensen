package dk.langli.jensen.broker;

import java.lang.reflect.Method;
import java.util.List;

class MethodCall {
	private Class<?> subjectClass;
	private Method method = null;
	private List<? extends Object> params = null;
	
	public MethodCall(Class<?> subjectClass, Method method, List<? extends Object> params) {
		this.subjectClass = subjectClass;
		this.method = method;
		this.params = params;
	}

	public Method getMethod() {
		return method;
	}

	public List<? extends Object> getParams() {
		return params;
	}

	public Class<?> getSubjectClass() {
		return subjectClass;
	}
}
