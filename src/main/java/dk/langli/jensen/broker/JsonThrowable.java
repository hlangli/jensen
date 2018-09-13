package dk.langli.jensen.broker;

import java.util.Map;

import dk.langli.jensen.Request;

class JsonThrowable extends JsonCause {
	private Request request = null;

	public JsonThrowable(Throwable target) {
		super(target, null);
	}

	public JsonThrowable(Throwable target, Map<String, Object> data, Request request, ExceptionUnwrapFilter exceptionUnwrapFilter) {
		super(target, data, exceptionUnwrapFilter);
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
