package com.tutkowski.thevoice.http;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpServer;
import com.tutkowski.thevoice.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

@Singleton
public class Server {
    private final HttpServer httpServer;

    @Inject
    public Server(Config config, Set<IController> controllers) throws IOException {
        this.httpServer =  HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        for (IController controller : controllers) {
            this.httpServer.createContext(controller.getPath(), controller.getHandler());
        }
    }

    public void start() {
        this.httpServer.start();
    }
}
