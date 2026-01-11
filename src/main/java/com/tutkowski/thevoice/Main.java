package com.tutkowski.thevoice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.http.Server;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Starting TheVoice");
            Injector injector = Guice.createInjector(new AppModule());
            configureSentry(injector.getInstance(Config.class));

            Bot bot = injector.getInstance(Bot.class);
            bot.init();

            Server server = injector.getInstance(Server.class);
            server.start();
            LOGGER.info("TheVoice started");
        } catch (Exception e) {
            LOGGER.error("Failed to start TheVoice", e);
        }
    }

    private static void configureSentry(Config config) {
        String dsn = config.getSentryDsn();
        if (dsn == null || dsn.isBlank()) {
            LOGGER.info("Sentry disabled (missing SENTRY_DSN)");
            return;
        }

        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setTracesSampleRate(1.0);
            options.getLogs().setEnabled(true);
        });
        LOGGER.info("Sentry enabled");
    }
}
