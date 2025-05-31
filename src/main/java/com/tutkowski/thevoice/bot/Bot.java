package com.tutkowski.thevoice.bot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tutkowski.thevoice.bot.tasks.ScheduledTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class Bot {
    private final JDA jda;
    private ScheduledExecutorService taskExecutor;
    private final Set<ScheduledTask> tasks;

    @Inject
    public Bot(BotConfig config, Set<ScheduledTask> tasks) {
        this.jda = JDABuilder.createDefault(config.getToken()).build();
        this.taskExecutor = Executors.newScheduledThreadPool(4);
        this.tasks = tasks;
    }

    public void init() throws InterruptedException {
        this.jda.awaitReady();
        for (ScheduledTask task : tasks) {
            this.taskExecutor.scheduleAtFixedRate(
                    task.getTask(this),
                    task.getInitialDelaySeconds(),
                    task.getIntervalSeconds(),
                    TimeUnit.SECONDS
            );
        }
    }

    public void createMessage(String channelName, String message) {
        this.jda.getGuilds().forEach(guild -> guild.getTextChannelsByName(channelName, true).stream().findFirst().ifPresent(channel -> channel.sendMessage(message).queue()));
    }
}
