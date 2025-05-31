package com.tutkowski.thevoice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.controller.HealthController;

public class Main {
    public static void main(String[] args) {
        try {
            Injector injector = Guice.createInjector(new AppModule());

            Bot bot = injector.getInstance(Bot.class);
            bot.init();

            HealthController healthController = injector.getInstance(HealthController.class);
            healthController.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}