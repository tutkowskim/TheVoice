package com.tutkowski.thevoice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.bot.tasks.PostLeetCodeDailyQuestionTask;
import com.tutkowski.thevoice.bot.tasks.ScheduledTask;
import com.tutkowski.thevoice.clients.leetcode.LeetCode;
import com.tutkowski.thevoice.http.HealthController;
import com.tutkowski.thevoice.http.IController;
import com.tutkowski.thevoice.http.Server;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // Http
        bind(Server.class);
        Multibinder<IController> controllerBinder = Multibinder.newSetBinder(binder(), IController.class);
        controllerBinder.addBinding().to(HealthController.class);

        // Clients
        bind(LeetCode.class);

        // Bot
        bind(Bot.class);
        bind(Config.class);

        Multibinder<ScheduledTask> taskBinder = Multibinder.newSetBinder(binder(), ScheduledTask.class);
        taskBinder.addBinding().to(PostLeetCodeDailyQuestionTask.class);
    }
}
