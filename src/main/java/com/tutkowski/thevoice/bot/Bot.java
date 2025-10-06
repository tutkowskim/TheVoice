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

import java.util.Set;

@Singleton
public class Bot {
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
    }

    public void init() throws InterruptedException {
        this.jda.awaitReady();

        Scheduler scheduler = new Scheduler();
        for (ScheduledTask task : tasks) {
            scheduler.schedule(task.getCronSchedule(), task.getTask(this));
        }
        scheduler.start();
    }

    public void createMessage(String channelName, String message) {
        this.jda.getGuilds().forEach(guild -> guild.getTextChannelsByName(channelName, true).stream().findFirst().ifPresent(channel -> channel.sendMessage(message).queue()));
    }
}
