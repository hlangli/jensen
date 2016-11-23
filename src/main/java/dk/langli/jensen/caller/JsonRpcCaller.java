package dk.langli.jensen.caller;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import dk.langli.jensen.JsonRpcCallerBuilder;
import dk.langli.jensen.JsonRpcResponse;
import dk.langli.jensen.Request;

public class JsonRpcCaller {
    private final Transport transport;
    private final SortedSet<Integer> idSeq;
    private final ObjectMapper objectMapper;
    
    public JsonRpcCaller(JsonRpcCallerBuilder builder) {
        idSeq = new TreeSet<>();
        transport = builder.getTransport();
        objectMapper = builder.getObjectMapper() != null ? builder.getObjectMapper() : new ObjectMapper();
    }
    
    public <T> T call(String method, Type returnType, Object... params) throws JsonRpcException, TransportException {
        T returnValue = null;
        if(params == null) {
            params = new Object[0];
        }
        Integer id = null;
        if(returnType.getTypeName().equals("void")) {
            id = nextId();
        }
        Request request = new Request(id, method, Arrays.asList(params));
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            String responseJson = transport.send(requestJson);
            JsonRpcResponse response = objectMapper.readValue(responseJson, JsonRpcResponse.class);
            if(response.getError() != null) {
                JsonRpcException e = objectMapper.convertValue(response.getError().getData(), JsonRpcException.class);
                throw e;
            }
            else {
                if(id != null) {
                    JavaType typeReference = TypeFactory.defaultInstance().constructType(returnType);
                    returnValue = objectMapper.convertValue(response.getResult(), typeReference);
                }
            }
        }
        catch(JsonRpcException e) {
            throw e;
        }
        catch(TransportException e) {
            throw e;
        }
        catch(Exception e) {
            throw new JsonRpcException(e);
        }
        finally {
            removeId(id);
        }
        return returnValue;
    }
    
    private Integer nextId() {
        Integer id = 1;
        synchronized(idSeq) {
            Iterator<Integer> idSeqIterator = idSeq.iterator();
            while(idSeqIterator.hasNext() && id == idSeqIterator.next()) {
                id++;
            }
            idSeq.add(id);
        }
        return id;
    }
    
    private void removeId(Integer id) {
        synchronized(idSeq) {
            idSeq.remove(id);
        }
    }

    public <T> T callThis(Object... params) throws JsonRpcException, TransportException {
        return call(2, params);
    }

    @SuppressWarnings("unchecked")
    public <T> T call(int callStackDistance, Object... params) throws JsonRpcException, TransportException {
        StackTraceElement callStackSubject = new Throwable().getStackTrace()[callStackDistance];
        String className = callStackSubject.getClassName();
        String methodName = callStackSubject.getMethodName();
        Type returnType = null;
        try {
            Class<?> type = Class.forName(className);
            Method method = type.getMethod(methodName, getParameterTypes(params));
            returnType = method.getGenericReturnType();
        }
        catch(NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            throw new JsonRpcException(e);
        }
        methodName = className+"."+methodName;
        return (T) call(methodName, returnType, params);
    }
    
    private Class<?>[] getParameterTypes(Object[] params) {
        List<Class<?>> parameterTypes = new ArrayList<>();
        for(Object param: params) {
            parameterTypes.add(param.getClass());
        }
        return parameterTypes.toArray(new Class<?>[parameterTypes.size()]);
    }
}
