package com.tutkowski.thevoice.bot;

import com.google.inject.AbstractModule;

public class BotModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Bot.class);
        bind(BotConfig.class);
    }
}