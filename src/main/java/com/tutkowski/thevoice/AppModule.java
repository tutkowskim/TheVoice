package com.tutkowski.thevoice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.bot.listeners.AskMeAnyThingChannelListener;
import com.tutkowski.thevoice.bot.tasks.PostFactOfTheDayTask;
import com.tutkowski.thevoice.bot.tasks.PostLeetCodeDailyQuestionTask;
import com.tutkowski.thevoice.bot.tasks.ScheduledTask;
import com.tutkowski.thevoice.clients.gemini.Gemini;
import com.tutkowski.thevoice.clients.leetcode.LeetCode;
import com.tutkowski.thevoice.http.HealthController;
import com.tutkowski.thevoice.http.IController;
import com.tutkowski.thevoice.http.Server;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // Http
        bind(Server.class);
        Multibinder<IController> controllerBinder = Multibinder.newSetBinder(binder(), IController.class);
        controllerBinder.addBinding().to(HealthController.class);

        // Clients
        bind(LeetCode.class);
        bind(Gemini.class);

        // Bot
        bind(Bot.class);
        bind(Config.class);

        Multibinder<ListenerAdapter> listenerAdapterMultibinder = Multibinder.newSetBinder(binder(), ListenerAdapter.class);
        listenerAdapterMultibinder.addBinding().to(AskMeAnyThingChannelListener.class);

        Multibinder<ScheduledTask> taskBinder = Multibinder.newSetBinder(binder(), ScheduledTask.class);
        taskBinder.addBinding().to(PostLeetCodeDailyQuestionTask.class);
        taskBinder.addBinding().to(PostFactOfTheDayTask.class);
    }
}
