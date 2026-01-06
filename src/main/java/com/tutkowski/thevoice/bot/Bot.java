package com.tutkowski.thevoice.bot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tutkowski.thevoice.Config;
import com.tutkowski.thevoice.bot.tasks.ScheduledTask;
import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Singleton
public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final JDA jda;
    private final Set<ScheduledTask> tasks;

    @Inject
    public Bot(Config config, Set<ListenerAdapter> listeners, Set<ScheduledTask> tasks) {
        this.jda = JDABuilder.createDefault(config.getToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        this.tasks = tasks;

        for (ListenerAdapter lister : listeners) {
            this.jda.addEventListener(lister);
        }
        LOGGER.info("Bot initialized with {} listeners and {} scheduled tasks", listeners.size(), tasks.size());
    }

    public void init() throws InterruptedException {
        LOGGER.info("Waiting for Discord gateway to be ready");
        this.jda.awaitReady();
        LOGGER.info("Discord gateway ready");

        Scheduler scheduler = new Scheduler();
        for (ScheduledTask task : tasks) {
            scheduler.schedule(task.getCronSchedule(), task.getTask(this));
            LOGGER.info("Scheduled task {} with cron {}", task.getClass().getSimpleName(), task.getCronSchedule());
        }
        scheduler.start();
        LOGGER.info("Scheduler started");
    }

    public JDA getJda() {
        return jda;
    }

    public void createMessage(String channelName, String message) {
        this.jda.getGuilds().forEach(guild -> guild.getTextChannelsByName(channelName, true).stream().findFirst().ifPresentOrElse(
                channel -> channel.sendMessage(message).queue(
                        success -> LOGGER.info("Posted message to #{} in guild {}", channelName, guild.getName()),
                        error -> LOGGER.warn("Failed to post message to #{} in guild {}", channelName, guild.getName(), error)
                ),
                () -> LOGGER.warn("Channel #{} not found in guild {}", channelName, guild.getName())
        ));
    }
}
