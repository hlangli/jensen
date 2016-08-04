package dk.langli.jensen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Jensen {
    protected static final String JSONRPC = "2.0";
    private Logger log = LoggerFactory.getLogger(Jensen.class);
    private ObjectMapper mapper = null;
    private ReturnValueHandler returnValueHandler = null;
    private ResponseHandler responseHandler = null;
    private InvocationIntercepter invocationIntercepter = null;
    private InstanceLocator instanceLocator = null;
    private PrettyPrinter prettyPrinter = null;
    private SecurityFilter securityFilter = null;
    
    public Jensen(JensenBuilder builder) {
        mapper = builder.getObjectMapper();
        returnValueHandler = builder.getReturnValueHandler();
        responseHandler = builder.getResponseHandler();
        invocationIntercepter = builder.getInvocationIntercepter();
        instanceLocator = builder.getInstanceLocator();
        prettyPrinter = builder.getPrettyPrinter();
        securityFilter = builder.getSecurityFilter();
        init();
    }

    public Jensen() {
        init();
    }
    
    private void init() {
        if(mapper == null) {
            mapper = new ObjectMapper();
        }
        if(instanceLocator == null) {
            instanceLocator = new DefaultInstanceLocator();
        }
    }

    public String invoke(String jsonRequest) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(jsonRequest, out);
        return out.size() == 0 ? null : out.toString();
    }

    public void invoke(InputStream jsonRequest, OutputStream out) {
        String request = null;
        try {
            request = IOUtils.toString(jsonRequest);
        }
        catch(IOException e) {
            log.error("Cannot read jsonRequest from InputStream", e);
        }
        invoke(request, out);
    }

    public void invoke(String jsonRequest, OutputStream out) {
        Response response = null;
        Request request = null;
        Integer id = null;
        try {
            request = mapper.readValue(jsonRequest, Request.class);
            id = request != null ? request.getId() : null;
            if(request != null) {
                if(request.getJsonrpc().equals(JSONRPC)) {
                    MethodCall methodCall = getMethodCall(request);
                    if(securityFilter != null && !securityFilter.isAllowed(methodCall, request)) {
                        String message = String.format("Invocation of %s not allowed", request.getMethod());
                        throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new SecurityException(message), request));
                    }
                    else {
                        Object result = invoke(methodCall, request);
                        if(returnValueHandler != null) {
                            result = returnValueHandler.onReturnValue(result);
                        }
                        response = new Response(id, result, null);
                        if(responseHandler != null) {
                            response = responseHandler.onResponse(response);
                        }
                    }
                }
                else {
                    throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("JSON-RPC " + request.getJsonrpc() + " is unsupported.  Use " + JSONRPC + " instead"), request));
                }
            }
            else {
                throw new JsonRpcException(JsonRpcError.INVALID_REQUEST.toError(new Exception("Request is missing"), request));
            }
        }
        catch(JsonRpcException e) {
            response = new Response(id, null, e.getError());
        }
        catch(Exception e) {
            response = new Response(id, null, JsonRpcError.PARSE_ERROR.toError(e, request));
        }
        if(id != null || request == null) {
            try {
                mapper.writeValue(out, response);
            }
            catch(JsonGenerationException e) {
                tryWrite(new Response(id, null, JsonRpcError.INTERNAL_ERROR.toError(e, request)), out);
            }
            catch(JsonMappingException e) {
                tryWrite(new Response(id, null, JsonRpcError.INTERNAL_ERROR.toError(e, request)), out);
            }
            catch(IOException e) {
                tryWrite(new Response(id, null, JsonRpcError.INTERNAL_ERROR.toError(e, request)), out);
            }
        }
    }

    private void tryWrite(Response response, OutputStream out) {
        try {
            write(response, out);
        }
        catch(JsonGenerationException e) {
            log.error("Cannot write response to OutputStream", e);
        }
        catch(JsonMappingException e) {
            log.error("Cannot write response to OutputStream", e);
        }
        catch(IOException e) {
            log.error("Cannot write response to OutputStream", e);
        }
    }

    private void write(Response response, OutputStream out) throws JsonGenerationException, JsonMappingException, IOException {
        ObjectWriter writer = mapper.writer();
        if(prettyPrinter != null) {
            writer = writer.with(prettyPrinter);
        }
        writer.writeValue(out, response);
    }

    private Object invoke(MethodCall methodCall, Request request) throws JsonRpcException {
        Object result = null;
        Method method = methodCall.getMethod();
        String methodSignature = getMethodSignature(methodCall);
        try {
            log.trace("Call " + methodSignature);
            Object instance = null;
            if(!Modifier.isStatic(method.getModifiers())) {
                instance = getInstance(method.getDeclaringClass(), request);
            }
            if(invocationIntercepter != null) {
                invocationIntercepter.onBeforeInvocation(method, instance, methodCall.getParams());
            }
            result = method.invoke(instance, methodCall.getParams().toArray());
        }
        catch(IllegalArgumentException e) {
            String message = "Method call " + methodSignature + " cannot accept the parameters given (" + methodCall.getParams().size() + (methodCall.getParams().size() == method.getParameterTypes().length ? " = " : " != ") + method.getParameterTypes().length + ")";
            log.warn(message, e);
            throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message), request));
        }
        catch(IllegalAccessException e) {
            String message = "Method call " + methodSignature + " cannot be accessed";
            log.warn(message, e);
            throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message), request));
        }
        catch(InvocationTargetException e) {
            String message = "Method call " + methodSignature + " threw an Exception";
            log.warn(message, e);
            throw new JsonRpcException(JsonRpcError.SERVER_ERROR.toError(e.getTargetException(), request));
        }
        return result;
    }

    private String getMethodSignature(MethodCall methodCall) {
        Method method = methodCall.getMethod();
        List<Object> params = methodCall.getParams();
        String methodSignature = method.getName() + "(";
        for(int i = 0; params != null && i < params.size(); i++) {
            methodSignature += (i != 0 ? ", " : "") + params.get(i).getClass().getSimpleName();
        }
        methodSignature += ")";
        return methodSignature;
    }

    private Object getInstance(Class<?> clazz, Request request) throws JsonRpcException {
        Object instance = null;
        try {
            instance = instanceLocator.getInstance(clazz);
        }
        catch(InstantiationException e) {
            String message = "Class " + clazz.getSimpleName() + " cannot be instantiated with a no-args constructor";
            log.warn(message, e);
            throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message), request));
        }
        catch(IllegalAccessException e) {
            String message = "Class " + clazz.getSimpleName() + " must not be instantiated with a no-args constructor";
            log.warn(message, e);
            throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(new Exception(message), request));
        }
        return instance;
    }

    private MethodCall getMethodCall(Request request) throws JsonRpcException {
        MethodCall methodCall = null;
        String qualifiedMethodName = request.getMethod();
        String methodName = null;
        String className = null;
        try {
            methodName = qualifiedMethodName.substring(qualifiedMethodName.lastIndexOf('.') + 1);
            className = qualifiedMethodName.substring(0, qualifiedMethodName.lastIndexOf('.'));
            List<Object> requestParams = request.getParams();
            Class<?> clazz = Class.forName(className);
            methodCall = getMethodCall(clazz, methodName, requestParams);
        }
        catch(Throwable e) {
            throw new JsonRpcException(JsonRpcError.METHOD_NOT_FOUND.toError(e, request));
        }
        if(methodCall == null) {
            String message = "No method \"" + methodName + "\" in class " + className + " can take the given parameter types";
            log.warn(message);
            throw new JsonRpcException(JsonRpcError.INVALID_PARAMS.toError(new Exception(message), request));
        }
        return methodCall;
    }

    private MethodCall getMethodCall(Class<?> clazz, String methodName, List<Object> requestParams) {
        MethodCall methodCall = null;
        Method[] methods = clazz.getMethods();
        int methodIndex = 0;
        while(methodCall == null && methodIndex < methods.length) {
            Method method = methods[methodIndex++];
            if(method.getName().equals(methodName)) {
                if(Modifier.isPublic(method.getModifiers())) {
                    log.trace("Check method parameter compatibility: " + method.getName() + "(" + toString(method.getParameterTypes()) + ")");
                    try {
                        List<Object> params = deserializeParameterList(requestParams, method.getParameterTypes());
                        if(params.size() == requestParams.size()) {
                            log.trace(method.getName() + "(" + toString(method.getParameterTypes()) + ") is compatible with the parameter list");
                            methodCall = new MethodCall(method, params);
                        }
                    }
                    catch(Throwable e) {
                        log.error(method.getName() + "() does not match");
                        // FIXME: Swallow and ignore - Bad coding practice.
                    }
                }
            }
        }
        return methodCall;
    }

    private String toString(Class<?>[] parameterTypes) {
        String parmTypes = "";
        for(int i = 0; parameterTypes != null && i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            parmTypes += (i != 0 ? ", " : "") + type.getSimpleName();
        }
        return parmTypes;
    }

    private List<Object> deserializeParameterList(List<Object> params, Class<?>[] parameterTypes) throws JsonParseException, JsonMappingException, IOException {
        List<Object> deserializedparams = new ArrayList<Object>();
        if(params.size() == parameterTypes.length) {
            for(int i = 0; i < parameterTypes.length; i++) {
                Object o = mapper.readValue(mapper.writeValueAsBytes(params.get(i)), parameterTypes[i]);
                deserializedparams.add(o);
            }
        }
        return deserializedparams;
    }

    public ReturnValueHandler getReturnValueHandler() {
        return returnValueHandler;
    }

    public void setReturnValueHandler(ReturnValueHandler returnValueHandler) {
        this.returnValueHandler = returnValueHandler;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public InvocationIntercepter getInvocationIntercepter() {
        return invocationIntercepter;
    }

    public void setInvocationIntercepter(InvocationIntercepter invocationIntercepter) {
        this.invocationIntercepter = invocationIntercepter;
    }

    public InstanceLocator getInstanceFinder() {
        return instanceLocator;
    }

    public void setInstanceFinder(InstanceLocator instanceFinder) {
        this.instanceLocator = instanceFinder;
    }
}
