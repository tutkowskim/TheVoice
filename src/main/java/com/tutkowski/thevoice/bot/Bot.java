package com.tutkowski.thevoice.bot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Singleton
public class Bot {
    private final JDA jda;

    @Inject
    public Bot(BotConfig config) {
        this.jda = JDABuilder.createDefault(config.getToken())
                .build();
    }

    public void init()  throws InterruptedException {
        this.jda.awaitReady();
    }
}
