package dk.langli.jensen.caller.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class JsonHttp {
	private URL url = null;
	private Map<Class<? extends HttpRequestBase>, Map<String, String>> methodParams = new HashMap<Class<? extends HttpRequestBase>, Map<String, String>>();
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, List<String>> headers = new HashMap<>();
	
	public JsonHttp(String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	public void addParam(String name, String value) {
		params.put(name, value);
	}

	public void addHeader(String name, String value) {
		headers.computeIfAbsent(name, k -> new ArrayList<>());
		headers.get(name).add(value);
	}
	
	public void addAuthentication(String username, String password) {
		String credentials = String.format("%s:%s", username, password);
		addHeader("Authorization", "Basic "+Base64.getEncoder().encodeToString(credentials.getBytes()));
	}

	@SuppressWarnings("unchecked")
    public void addParam(String name, String value, Class<? extends HttpRequestBase>... requestMethods) {
		for(Class<? extends HttpRequestBase> requestMethod: requestMethods) {
			Map<String, String> params = methodParams.get(requestMethod);
			if(params == null) {
				params = new HashMap<String, String>();
				methodParams.put(requestMethod, params);
			}
			params.put(name, value);
		}
	}
	
	public void removeParam(String name) {
		params.remove(name);
	}

	@SuppressWarnings("unchecked")
    public void removeParam(String name, Class<? extends HttpRequestBase>... requestMethods) {
		for(Class<? extends HttpRequestBase> requestMethod: requestMethods) {
			Map<String, String> params = methodParams.get(requestMethod);
			if(params != null) {
				params.remove(name);
			}
		}
	}
	
	private String url(String path, Map<String, String> params) {
		String url = this.url+"/"+path;
		boolean urlContainsParams = url.indexOf('?') > -1;
		for(String name: this.params.keySet()) {
			url = url+(urlContainsParams ? "&" : "?")+name+"="+this.params.get(name);
			urlContainsParams = true;
		}
		if(params != null) {
			Set<String> parameterNames = params.keySet();
			if(parameterNames != null) {
				for(String name: parameterNames) {
					url = url+(urlContainsParams ? "&" : "?")+name+"="+params.get(name);
					urlContainsParams = true;
				}
			}
		}
		return url;
	}
	
	private Map<String, String> putParams() {
		return methodParams.get(HttpPut.class);
	}
	
	public HttpInputStream put(String path) throws ClientProtocolException, IOException {
		return execute(new HttpPut(url(path, putParams())));
	}
	
	public HttpInputStream put(String path, String json) throws ClientProtocolException, IOException {
		return execute(withJson(new HttpPut(url(path, putParams())), json));
	}
	
	private Map<String, String> postParams() {
		return methodParams.get(HttpPost.class);
	}
	
	public HttpInputStream post(String path) throws ClientProtocolException, IOException {
		return execute(new HttpPost(url(path, postParams())));
	}
	
	public HttpInputStream post(String path, String json) throws ClientProtocolException, IOException {
		return execute(withJson(new HttpPost(url(path, postParams())), json));
	}
	
	private Map<String, String> getParams() {
		return methodParams.get(HttpGet.class);
	}
	
	public HttpInputStream get(String path) throws ClientProtocolException, IOException {
		return execute(new HttpGet(url(path, getParams())));
	}

	public HttpInputStream get(String path, String json) throws ClientProtocolException, IOException {
		return execute(new HttpGet(url(path, getParams())));
	}
	
	private Map<String, String> deleteParams() {
		return methodParams.get(HttpDelete.class);
	}
	
	public HttpInputStream delete(String path) throws ClientProtocolException, IOException {
		return execute(new HttpDelete(url(path, deleteParams())));
	}
	
	private HttpEntityEnclosingRequestBase withJson(HttpEntityEnclosingRequestBase request, String json) {
		if(json != null) {
			HttpEntity entity = new StringEntity(json, "UTF-8");
			request.setHeader("Content-Type", "application/json;charset=UTF-8");
			request.setEntity(entity);
		}
		return request;
	}
	
	private HttpInputStream execute(HttpUriRequest request) throws ClientProtocolException, IOException {
		CloseableHttpClient http = HttpClients.createDefault();
		for(String name: headers.keySet()) {
			for(String value: headers.get(name)) {
				request.addHeader(name, value);
			}
		}
		CloseableHttpResponse response = http.execute(request);
		return new HttpInputStream(http, response, response.getEntity().getContent());
	}
}
