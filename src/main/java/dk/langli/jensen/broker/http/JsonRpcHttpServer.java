package dk.langli.jensen.broker.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import dk.langli.jensen.broker.JsonRpcBroker;

public class JsonRpcHttpServer {
    private final JsonRpcBroker broker;
    private final Server server;
    
    public JsonRpcHttpServer(JsonRpcBroker broker) {
        this(broker, new InetSocketAddress(0));
    }

    public JsonRpcHttpServer(JsonRpcBroker broker, int port) {
        this(broker, new InetSocketAddress(port));
    }

    public JsonRpcHttpServer(JsonRpcBroker broker, String host, int port) {
        this(broker, new InetSocketAddress(host, port));
    }

    public JsonRpcHttpServer(JsonRpcBroker broker, InetSocketAddress address) {
        this.broker = broker;
        server = new Server(address);
    }
    
    public void start() throws Exception {
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                broker.invoke(request.getInputStream(), response.getOutputStream());
            }
        });
        server.start();
    }
    
    public void stop() throws Exception {
        server.stop();
    }
    
    public void join() throws InterruptedException {
        server.join();
    }
    
    public int getPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }
}
