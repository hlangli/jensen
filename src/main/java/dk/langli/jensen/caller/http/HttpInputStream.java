package dk.langli.jensen.caller.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpInputStream extends FilterInputStream {
	private CloseableHttpClient http = null;
	private CloseableHttpResponse response = null;

	public HttpInputStream(CloseableHttpClient http, CloseableHttpResponse response, InputStream httpContent) {
		super(httpContent);
		this.http = http;
		this.response = response;
	}

	@Override
	public int read() throws IOException {
		int b = super.read();
		if(b == -1) {
			close();
		}
		return b;
	}

	@Override
	public void close() throws IOException {
		super.close();
		EntityUtils.consume(response.getEntity());
		response.close();
		http.close();
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public int status() {
		return response.getStatusLine().getStatusCode();
	}
}
