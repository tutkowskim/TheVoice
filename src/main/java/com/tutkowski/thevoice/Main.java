package com.tutkowski.thevoice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.http.Server;

public class Main {
    public static void main(String[] args) {
        try {
            Injector injector = Guice.createInjector(new AppModule());

            Bot bot = injector.getInstance(Bot.class);
            bot.init();

            Server server = injector.getInstance(Server.class);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}