package com.tutkowski.thevoice.http;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.net.httpserver.HttpServer;
import com.tutkowski.thevoice.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

@Singleton
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final HttpServer httpServer;

    @Inject
    public Server(Config config, Set<IController> controllers) throws IOException {
        int port = config.getPort();
        this.httpServer =  HttpServer.create(new InetSocketAddress(port), 0);
        for (IController controller : controllers) {
            this.httpServer.createContext(controller.getPath(), controller.getHandler());
            LOGGER.info("Registered HTTP handler {}", controller.getPath());
        }
        LOGGER.info("HTTP server configured on port {}", port);
    }

    public void start() {
        this.httpServer.start();
        LOGGER.info("HTTP server started");
    }
}
