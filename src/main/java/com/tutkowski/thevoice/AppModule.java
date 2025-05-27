package com.tutkowski.thevoice;

import com.google.inject.AbstractModule;
import com.tutkowski.thevoice.bot.BotModule;
import com.tutkowski.thevoice.controller.HealthController;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new BotModule());
        bind(HealthController.class);
    }
}
