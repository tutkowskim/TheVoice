package com.tutkowski.thevoice.bot.tasks;

import com.tutkowski.thevoice.bot.Bot;

public interface ScheduledTask {
    Runnable getTask(Bot bot);
    long getInitialDelaySeconds();
    long getIntervalSeconds();
}