package com.tutkowski.thevoice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.bot.BotConfig;
import com.tutkowski.thevoice.bot.tasks.PostLeetCodeDailyQuestionTask;
import com.tutkowski.thevoice.bot.tasks.ScheduledTask;
import com.tutkowski.thevoice.clients.leetcode.LeetCode;
import com.tutkowski.thevoice.controller.HealthController;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // Controllers
        bind(HealthController.class);;

        // Clients
        bind(LeetCode.class);

        // Bot
        bind(Bot.class);
        bind(BotConfig.class);

        Multibinder<ScheduledTask> taskBinder = Multibinder.newSetBinder(binder(), ScheduledTask.class);
        taskBinder.addBinding().to(PostLeetCodeDailyQuestionTask.class);
    }
}
