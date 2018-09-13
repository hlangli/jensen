package dk.langli.jensen.broker;

import java.util.Map;

import dk.langli.jensen.Request;

public enum JsonRpcError {
	PARSE_ERROR(-32700, "Parse error"),
	INVALID_REQUEST(-32600, "Invalid Request"),
	METHOD_NOT_FOUND(-32601, "Method not found"),
	INVALID_PARAMS(-32602, "Invalid params"),
	INTERNAL_ERROR(-32603, "Internal error"),
	SERVER_ERROR(-32000, "Server error");

	private Integer code = null;
	private String message = null;

	private JsonRpcError(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Error toError(Request request, ExceptionUnwrapFilter exceptionUnwrapFilter) {
		return toError((Throwable) null, request, exceptionUnwrapFilter);
	}

	@Deprecated
	public Error toError(Map<String, Object> data, Request request) {
		return new Error(code, message, data);
	}

	public Error toError(Throwable e, Request request, ExceptionUnwrapFilter exceptionUnwrapFilter) {
		return toError(e, null, request, exceptionUnwrapFilter);
	}

	public Error toError(Throwable e, Map<String, Object> data, Request request, ExceptionUnwrapFilter exceptionUnwrapFilter) {
		return new Error(code, message, new JsonThrowable(e, data, request, exceptionUnwrapFilter));
	}
}
