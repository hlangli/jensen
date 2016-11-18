package dk.langli.jensen;

import java.util.Map;

class JsonThrowable extends JsonCause {
	private Request request = null;

	public JsonThrowable(Throwable target) {
		super(target, null);
	}

	public JsonThrowable(Throwable target, Map<String, Object> data, Request request) {
		super(target, data);
	    this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
